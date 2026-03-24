package me.pinfort.tsvideos.core.external.database.dto.converter

import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.database.dto.SplittedFileDto
import org.springframework.stereotype.Component

@Component
class SplittedFileStatusConverter {
    fun convert(dto: SplittedFileDto.Status): SplittedFile.Status =
        when (dto) {
            SplittedFileDto.Status.REGISTERED -> SplittedFile.Status.REGISTERED
            SplittedFileDto.Status.COMPRESS_SAVED -> SplittedFile.Status.COMPRESS_SAVED
            SplittedFileDto.Status.ENCODE_TASK_ADDED -> SplittedFile.Status.ENCODE_TASK_ADDED
        }
}
