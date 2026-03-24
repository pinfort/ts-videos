package me.pinfort.tsvideos.core.external.database.dto.converter

import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.external.database.dto.ExecutedFileDto
import org.springframework.stereotype.Component

@Component
class ExecutedFileStatusConverter {
    fun convert(status: ExecutedFileDto.Status): ExecutedFile.Status =
        when (status) {
            ExecutedFileDto.Status.REGISTERED -> ExecutedFile.Status.REGISTERED
            ExecutedFileDto.Status.DROPCHECKED -> ExecutedFile.Status.DROPCHECKED
            ExecutedFileDto.Status.SPLITTED -> ExecutedFile.Status.SPLITTED
        }
}
