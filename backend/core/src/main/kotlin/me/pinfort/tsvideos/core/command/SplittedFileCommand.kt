package me.pinfort.tsvideos.core.command

import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.database.mapper.SplittedFileMapper
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
class SplittedFileCommand(
    private val splittedFileMapper: SplittedFileMapper,
    private val logger: Logger,
) {
    fun delete(
        splittedFile: SplittedFile,
        dryRun: Boolean = false,
    ) {
        if (!dryRun) {
            splittedFileMapper.delete(splittedFile.id)
        }
        logger.info("Delete splitted file, id=${splittedFile.id}, splittedFile=$splittedFile")
    }
}
