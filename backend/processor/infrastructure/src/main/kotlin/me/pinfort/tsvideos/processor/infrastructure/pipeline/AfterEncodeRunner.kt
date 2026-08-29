package me.pinfort.tsvideos.processor.infrastructure.pipeline

import me.pinfort.tsvideos.core.command.CreatedFileCommand
import me.pinfort.tsvideos.core.command.ExecutedFileCommand
import me.pinfort.tsvideos.core.command.ProgramCommand
import me.pinfort.tsvideos.core.command.SplittedFileCommand
import me.pinfort.tsvideos.core.component.DirectoryNameComponent
import me.pinfort.tsvideos.core.component.MimeTypeComponent
import me.pinfort.tsvideos.core.component.ValidateCompletedComponent
import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.samba.NasComponent
import me.pinfort.tsvideos.core.external.samba.SambaClient
import me.pinfort.tsvideos.processor.infrastructure.external.slack.SlackClient
import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Path

/**
 * Amatsukaze のエンコード実行後バッチから呼び出される処理。
 * エンコード済みファイルを created_file として登録し、NAS へアップロードしたうえで
 * 元ファイルを削除し、番組を COMPLETED にする。
 *
 * FileProcessingPipeline と異なりロールバックは行わない。
 * NAS へのアップロードとローカルファイルの削除が済んだ後で失敗を巻き戻すことはできないため、
 * 失敗時はログと Slack 通知を行い番組を ERROR にするだけに留める。
 */
@Component
class AfterEncodeRunner(
    private val splittedFileCommand: SplittedFileCommand,
    private val createdFileCommand: CreatedFileCommand,
    private val executedFileCommand: ExecutedFileCommand,
    private val programCommand: ProgramCommand,
    private val nasComponent: NasComponent,
    private val sambaClient: SambaClient,
    private val directoryNameComponent: DirectoryNameComponent,
    private val mimeTypeComponent: MimeTypeComponent,
    private val validateCompletedComponent: ValidateCompletedComponent,
    private val slackClient: SlackClient,
    private val logger: Logger,
) {
    /**
     * Amatsukaze が実行後バッチに渡す環境変数のうち、この処理で使うもの。
     *
     * @param itemId Amatsukaze のアイテムID
     * @param inPath 入力ファイルパス（Amatsukaze により succeeded ディレクトリへ移動済み）
     * @param files 出力ファイル群
     * @param success エンコードの成否
     * @param errorMessage エラーメッセージ（失敗したときのみ）
     */
    data class Input(
        val itemId: Int,
        val inPath: Path,
        val files: List<Path>,
        val success: Boolean,
        val errorMessage: String,
    )

    private data class RegisteredFile(
        val localFile: File,
        val createdFile: CreatedFile,
    )

    fun run(
        input: Input,
        dryRun: Boolean = false,
        onUploadProgress: (bytesTransferred: Long, totalBytes: Long) -> Unit = { _, _ -> },
    ) {
        if (!input.success) {
            notifyError(input)
            return
        }

        val splittedFile = findOriginalSplittedFile(input.inPath)
        if (splittedFile == null) {
            // executedFileId が取れないと後続の完了処理ができないため、何も登録せずに中断する
            logger.error("original splitted file not found, inPath=${input.inPath}")
            slackClient.notify("original splitted file not found. in_path:${input.inPath}")
            return
        }

        val registeredFiles = registerFiles(splittedFile, input.files, dryRun)
        moveFiles(registeredFiles, dryRun, onUploadProgress)
        finishProcess(splittedFile.executedFileId, input.inPath, dryRun)
        logger.info("uploading file completed, targets=${input.files}")
    }

    private fun notifyError(input: Input) {
        logger.error("encode failed, amatsukazeId=${input.itemId}, inPath=${input.inPath}, reason=${input.errorMessage}")
        slackClient.notify(
            "encode failed.\namatsukaze_id:${input.itemId},\nin_path:${input.inPath},\n`reason:${input.errorMessage}`",
        )
    }

    /**
     * Amatsukaze が入力ファイルを succeeded ディレクトリへ移動しているため、
     * 1つ上の階層に元のパスを組み立て直して splitted_file を探す。
     */
    private fun findOriginalSplittedFile(inPath: Path): SplittedFile? {
        val originalFile = inPath.parent.parent.resolve(inPath.fileName)
        return splittedFileCommand.findByFile(originalFile.toFile().absolutePath)
    }

    private fun registerFiles(
        splittedFile: SplittedFile,
        files: List<Path>,
        dryRun: Boolean,
    ): List<RegisteredFile> =
        files.map { path ->
            val localFile = path.toFile()
            val mimeType = mimeTypeComponent.guess(localFile.name)
            val createdFile =
                createdFileCommand.insert(
                    splittedFile.id,
                    nasTargetFile(path),
                    localFile.length(),
                    mimeType.mime,
                    mimeType.encoding,
                    CreatedFile.Status.ENCODE_SUCCESS,
                    dryRun,
                )
            logger.info("created file registered, file=$localFile, splittedFileId=${splittedFile.id}")
            RegisteredFile(localFile, createdFile)
        }

    /**
     * 出力ファイルは <録画ディレクトリ>/tssplitter/encoded/ に置かれるため、2つ上の tssplitter
     * ディレクトリを DirectoryNameComponent に渡すと録画ディレクトリ名が得られる。
     * FileProcessingPipeline の圧縮ファイルと異なり、エンコード済みファイルは VIDEO_STORE_NAS に置く。
     */
    private fun nasTargetFile(file: Path): String {
        val splittedFileDirectory = file.parent.parent
        val bucket = directoryNameComponent.indexDirectoryName(splittedFileDirectory)
        val programDirectory = directoryNameComponent.programDirectoryName(splittedFileDirectory)
        return sambaClient.resolvePathUnderBaseDir(
            SambaClient.NasType.VIDEO_STORE_NAS,
            "$bucket/$programDirectory/${file.fileName}",
        )
    }

    private fun moveFiles(
        registeredFiles: List<RegisteredFile>,
        dryRun: Boolean,
        onUploadProgress: (bytesTransferred: Long, totalBytes: Long) -> Unit,
    ) {
        registeredFiles.forEach { (localFile, createdFile) ->
            if (!dryRun) {
                nasComponent.uploadResource(
                    localFile,
                    createdFile.file,
                    SambaClient.NasType.VIDEO_STORE_NAS,
                    onUploadProgress,
                )
            }
            createdFileCommand.updateStatus(createdFile, CreatedFile.Status.FILE_MOVED, dryRun)
            logger.info("file uploaded, file=${localFile.name}, target=${createdFile.file}")
            deleteLocalFile(localFile, dryRun)
        }
    }

    private fun finishProcess(
        executedFileId: Long,
        inPath: Path,
        dryRun: Boolean,
    ) {
        val program = programCommand.findByExecutedFileId(executedFileId)
        if (program == null) {
            logger.error("program not found, executedFileId=$executedFileId")
            return
        }

        try {
            if (validateCompletedComponent.validate(program.id)) {
                logger.info("program valid, status will be completed, programId=${program.id}")
                deleteLocalFile(inPath.toFile(), dryRun)
                removeExecutedFile(executedFileId, dryRun)
                programCommand.updateStatusByExecutedFileId(executedFileId, Program.Status.COMPLETED, dryRun)
            } else {
                logger.error("program invalid, programId=${program.id}")
                slackClient.notify("program invalid. programId:${program.id}")
                programCommand.updateStatusByExecutedFileId(executedFileId, Program.Status.ERROR, dryRun)
            }
        } catch (e: Exception) {
            logger.error("program invalid, programId=${program.id}", e)
            slackClient.notify(
                "program invalid. programId:${program.id}, e:${e.message}\nstackTrace:\n```\n${e.stackTraceToString()}\n```",
            )
            programCommand.updateStatusByExecutedFileId(executedFileId, Program.Status.ERROR, dryRun)
        }
    }

    /**
     * TSファイル一連の処理の元ファイルを削除する。DBのレコードは残す。
     */
    private fun removeExecutedFile(
        executedFileId: Long,
        dryRun: Boolean,
    ) {
        logger.info("executed file to be deleted, id=$executedFileId")
        val executedFile = executedFileCommand.find(executedFileId)
        if (executedFile == null) {
            logger.warn("executed file not found, id=$executedFileId")
            return
        }
        deleteLocalFile(File(executedFile.file), dryRun)
        splittedFileCommand.selectByExecutedFileId(executedFileId).forEach {
            deleteLocalFile(File(it.file), dryRun)
        }
    }

    private fun deleteLocalFile(
        file: File,
        dryRun: Boolean,
    ) {
        if (!file.exists()) return
        logger.info("removing file, file=$file")
        if (!dryRun) {
            file.delete()
        }
    }
}
