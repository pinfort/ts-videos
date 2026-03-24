package me.pinfort.tsvideos.core.external.database.dto.converter

import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.external.database.dto.CreatedFileDto
import org.springframework.stereotype.Component

@Component
class CreatedFileConverter(
    private val createdFileStatusConverter: CreatedFileStatusConverter,
) {
    fun convert(dto: CreatedFileDto): CreatedFile =
        CreatedFile(
            id = dto.id,
            splittedFileId = dto.splittedFileId,
            file = dto.file,
            size = dto.size,
            mime = dto.mime,
            encoding = dto.encoding,
            status = createdFileStatusConverter.convert(dto.status),
        )
}
