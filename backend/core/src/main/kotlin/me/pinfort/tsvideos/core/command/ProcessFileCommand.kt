package me.pinfort.tsvideos.core.command

import me.pinfort.tsvideos.core.component.CompressComponent
import me.pinfort.tsvideos.core.component.DirectoryNameComponent
import me.pinfort.tsvideos.core.component.MainSplittedFileFinderComponent
import me.pinfort.tsvideos.core.config.ProcessorToolConfigurationProperties
import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.domain.FileName
import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.exception.TsVideosException
import me.pinfort.tsvideos.core.external.samba.NasComponent
import me.pinfort.tsvideos.core.external.samba.SambaClient
import me.pinfort.tsvideos.core.external.tool.AmatsukazeAddTaskClient
import me.pinfort.tsvideos.core.external.tool.DropChkClient
import me.pinfort.tsvideos.core.external.tool.DurationProbeClient
import me.pinfort.tsvideos.core.external.tool.TsSplitterClient
import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.io.File
import kotlin.math.ceil

/**
 * DropChk -> TsSplitter -> CompressAndSave -> AmatsukazeAddTask の4段パイプライン。
 * 各段は失敗すると自身とそれ以前の段を逆順にロールバックしてから例外を再送出する。
 */
@Component
class ProcessFileCommand(
    private val executedFileCommand: ExecutedFileCommand,
    private val splittedFileCommand: SplittedFileCommand,
    private val createdFileCommand: CreatedFileCommand,
    private val programCommand: ProgramCommand,
    private val dropChkClient: DropChkClient,
    private val tsSplitterClient: TsSplitterClient,
    private val amatsukazeAddTaskClient: AmatsukazeAddTaskClient,
    private val durationProbeClient: DurationProbeClient,
    private val mainSplittedFileFinderComponent: MainSplittedFileFinderComponent,
    private val compressComponent: CompressComponent,
    private val nasComponent: NasComponent,
    private val sambaClient: SambaClient,
    private val directoryNameComponent: DirectoryNameComponent,
    private val processorToolConfigurationProperties: ProcessorToolConfigurationProperties,
    private val logger: Logger,
) {
    enum class Result {
        PROCESSED,
        SKIPPED_ALREADY_REGISTERED,
    }

    private sealed class DropChkOutcome {
        data class Registered(
            val executedFile: ExecutedFile,
        ) : DropChkOutcome()

        data class AlreadyExists(
            val program: Program,
        ) : DropChkOutcome()
    }

    fun processFile(
        file: File,
        dryRun: Boolean = false,
        onUploadProgress: (bytesTransferred: Long, totalBytes: Long) -> Unit = { _, _ -> },
    ): Result {
        val dropChkOutcome =
            try {
                dropChk(file, dryRun)
            } catch (e: Exception) {
                rollbackDropChk(file, dryRun)
                throw e
            }

        val executedFile =
            when (dropChkOutcome) {
                is DropChkOutcome.AlreadyExists -> {
                    logger.info("Program already registered, skip processing, file=$file, program=${dropChkOutcome.program}")
                    return Result.SKIPPED_ALREADY_REGISTERED
                }
                is DropChkOutcome.Registered -> dropChkOutcome.executedFile
            }

        val mainSplittedFile =
            try {
                tsSplit(executedFile, dryRun)
            } catch (e: Exception) {
                rollbackTsSplit(executedFile, dryRun)
                rollbackDropChk(file, dryRun)
                throw e
            }

        try {
            compressAndSave(mainSplittedFile, dryRun, onUploadProgress)
        } catch (e: Exception) {
            rollbackCompressAndSave(mainSplittedFile, dryRun)
            rollbackTsSplit(executedFile, dryRun)
            rollbackDropChk(file, dryRun)
            throw e
        }

        try {
            amatsukazeAddTask(mainSplittedFile)
        } catch (e: Exception) {
            rollbackAmatsukazeAddTask(mainSplittedFile)
            rollbackCompressAndSave(mainSplittedFile, dryRun)
            rollbackTsSplit(executedFile, dryRun)
            rollbackDropChk(file, dryRun)
            throw e
        }

        return Result.PROCESSED
    }

    // Stage 1: drop-frame check, then register executed_file + program
    private fun dropChk(
        file: File,
        dryRun: Boolean,
    ): DropChkOutcome {
        if (!file.exists()) {
            throw TsVideosException("file not found, file=$file")
        }

        programCommand.findByName(file.name)?.let { return DropChkOutcome.AlreadyExists(it) }

        val drops = dropChkClient.check(file)
        val fileName = FileName.fromFileNameString(file.name)
        val duration = durationProbeClient.probe(file)

        val executedFile =
            executedFileCommand.insert(
                file = file.absolutePath,
                drops = drops,
                size = file.length(),
                recordedAt = fileName.recordedAt,
                channel = fileName.channel,
                title = fileName.title,
                channelName = fileName.channelName,
                duration = duration,
                dryRun = dryRun,
            )
        programCommand.insert(file.name, executedFile.id, dryRun)

        return DropChkOutcome.Registered(executedFile)
    }

    private fun rollbackDropChk(
        file: File,
        dryRun: Boolean,
    ) {
        val executedFile = executedFileCommand.findByFile(file.absolutePath)
        if (executedFile == null) {
            logger.warn("No executed file to rollback, file=$file")
            return
        }
        programCommand.deleteByExecutedFileId(executedFile.id, dryRun)
        executedFileCommand.delete(executedFile, dryRun)
    }

    // Stage 2: split into elementary streams, register splitted_file rows, pick the main file
    private fun tsSplit(
        executedFile: ExecutedFile,
        dryRun: Boolean,
    ): SplittedFile {
        val originalFile = File(executedFile.file)
        if (!originalFile.exists()) {
            throw TsVideosException("file not found, file=$originalFile")
        }

        val outDir = File(originalFile.parentFile, "tssplitter")
        if (!outDir.exists()) {
            outDir.mkdirs()
        }

        if (findSplitFiles(originalFile, outDir).isNotEmpty()) {
            throw TsVideosException("splitted file already exists, originalFile=$originalFile")
        }

        val timeoutSec = maxOf(ceil(executedFile.duration).toLong(), 600L)
        val exitCode = tsSplitterClient.split(originalFile, outDir, timeoutSec)
        if (exitCode != 0) {
            throw TsVideosException("TsSplitter failed, exitCode=$exitCode, originalFile=$originalFile")
        }

        val foundFiles = findSplitFiles(originalFile, outDir)
        if (foundFiles.isEmpty()) {
            throw TsVideosException("no splitted file found, originalFile=$originalFile")
        }

        val insertedSplittedFiles =
            foundFiles.map { splitFile ->
                val duration = durationProbeClient.probe(splitFile)
                splittedFileCommand.insert(executedFile.id, splitFile.absolutePath, splitFile.length(), duration, dryRun)
            }
        executedFileCommand.updateStatus(executedFile, ExecutedFile.Status.SPLITTED, dryRun)

        return mainSplittedFileFinderComponent.find(executedFile, insertedSplittedFiles)
    }

    private fun rollbackTsSplit(
        executedFile: ExecutedFile,
        dryRun: Boolean,
    ) {
        val originalFile = File(executedFile.file)
        val outDir = File(originalFile.parentFile, "tssplitter")
        findSplitFiles(originalFile, outDir).forEach { it.delete() }
        splittedFileCommand.selectByExecutedFileId(executedFile.id).forEach { splittedFileCommand.delete(it, dryRun) }
    }

    private fun findSplitFiles(
        originalFile: File,
        outDir: File,
    ): List<File> {
        val stem = originalFile.nameWithoutExtension
        return outDir
            .listFiles { candidate -> candidate.name.startsWith(stem) && candidate.name.endsWith(".m2ts") }
            ?.sortedBy { it.name }
            ?: emptyList()
    }

    // Stage 3: gzip-compress the main split file and upload it to the original-store NAS
    private fun compressAndSave(
        splittedFile: SplittedFile,
        dryRun: Boolean,
        onUploadProgress: (bytesTransferred: Long, totalBytes: Long) -> Unit,
    ) {
        val splitFile = File(splittedFile.file)
        val compressedFile = File(splitFile.parentFile, "${splitFile.name}.gz")

        if (!compressComponent.compress(splitFile, compressedFile)) {
            logger.error("Compress skipped, compressed file already exists, splitFile=$splitFile")
            return
        }

        // splitFile.parentFile is the "tssplitter" directory; its parent is the original recording's directory,
        // whose normalized name is the NAS bucket/program-directory name.
        val tssplitterDir = splitFile.parentFile.toPath()
        val bucket = directoryNameComponent.indexDirectoryName(tssplitterDir)
        val programDirectory = directoryNameComponent.programDirectoryName(tssplitterDir)
        val relativeTargetFile = "$bucket/$programDirectory/${compressedFile.name}"
        // NAS の baseDir を含めた、共有ルートからの相対パスとして DB にも保存する。
        val targetFile = sambaClient.resolvePathUnderBaseDir(SambaClient.NasType.ORIGINAL_STORE_NAS, relativeTargetFile)

        nasComponent.uploadResource(compressedFile, targetFile, SambaClient.NasType.ORIGINAL_STORE_NAS, onUploadProgress)
        createdFileCommand.insert(splittedFile.id, targetFile, compressedFile.length(), "video/vnd.dlna.mpeg-tts", "gzip", dryRun)
        splittedFileCommand.updateStatus(splittedFile, SplittedFile.Status.COMPRESS_SAVED, dryRun)
        compressedFile.delete()
    }

    private fun rollbackCompressAndSave(
        splittedFile: SplittedFile,
        dryRun: Boolean,
    ) {
        createdFileCommand
            .selectBySplittedFileId(splittedFile.id)
            .filter { it.encoding == "gzip" }
            .forEach { createdFileCommand.delete(it, dryRun) }
    }

    // Stage 4: submit the main split file to the running Amatsukaze server
    private fun amatsukazeAddTask(splittedFile: SplittedFile) {
        val executedFile =
            executedFileCommand.find(splittedFile.executedFileId)
                ?: throw TsVideosException("executed file not found, id=${splittedFile.executedFileId}")

        val splitFile = File(splittedFile.file)
        val outDir = File(splitFile.parentFile, "encoded")
        if (!outDir.exists()) {
            outDir.mkdirs()
        }

        amatsukazeAddTaskClient.addTask(splitFile, outDir, decideProfile(executedFile))
    }

    private fun rollbackAmatsukazeAddTask(splittedFile: SplittedFile) {
        logger.info("Nothing to rollback for AmatsukazeAddTask, splittedFile=$splittedFile")
    }

    private val atxDivTitleRegex = Regex("#[0-9]{1,3}-#[0-9]{1,3}")

    private fun decideProfile(executedFile: ExecutedFile): String {
        val amatsukaze = processorToolConfigurationProperties.amatsukaze
        val isAtxDiv =
            executedFile.channelName == "ＡＴ－Ｘ" &&
                executedFile.duration > 10800 &&
                atxDivTitleRegex.containsMatchIn(executedFile.title)
        return if (isAtxDiv) amatsukaze.atxDivProfile else amatsukaze.defaultProfile
    }
}
