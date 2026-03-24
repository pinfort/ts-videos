package me.pinfort.tsvideos.core.external.database.dto.converter

import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.external.database.dto.CreatedFileDto
import org.springframework.stereotype.Component

@Component
class CreatedFileStatusConverter {
    fun convert(status: CreatedFileDto.Status): CreatedFile.Status =
        when (status) {
            CreatedFileDto.Status.REGISTERED -> CreatedFile.Status.REGISTERED
            CreatedFileDto.Status.FILE_MOVED -> CreatedFile.Status.FILE_MOVED
            CreatedFileDto.Status.ENCODE_SUCCESS -> CreatedFile.Status.ENCODE_SUCCESS
        }
}
