package me.pinfort.tsvideos.core.command

import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.external.database.dto.converter.ExecutedFileConverter
import me.pinfort.tsvideos.core.external.database.mapper.ExecutedFileMapper
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
class ExecutedFileCommand(
    private val executedFileMapper: ExecutedFileMapper,
    private val executedFileConverter: ExecutedFileConverter,
    private val logger: Logger,
) {
    fun find(id: Long): ExecutedFile? = executedFileMapper.find(id)?.let { executedFileConverter.convert(it) }

    fun delete(
        executedFile: ExecutedFile,
        dryRun: Boolean = false,
    ) {
        if (!dryRun) {
            executedFileMapper.delete(executedFile.id)
        }
        logger.info("Delete executed file, id=${executedFile.id}, executedFile=$executedFile")
    }
}
