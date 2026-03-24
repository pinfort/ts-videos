package me.pinfort.tsvideos.core.external.database.dto.converter

import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.database.dto.SplittedFileDto
import org.springframework.stereotype.Component

@Component
class SplittedFileConverter(
    private val convertStatus: SplittedFileStatusConverter
) {
    fun convert(dto: SplittedFileDto): SplittedFile {
        return SplittedFile(
            id = dto.id,
            executedFileId = dto.executedFileId,
            file = dto.file,
            size = dto.size,
            duration = dto.duration,
            status = convertStatus.convert(dto.status)
        )
    }
}
