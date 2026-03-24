package me.pinfort.tsvideos.core.external.database.dto.converter

import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.external.database.dto.ExecutedFileDto
import org.springframework.stereotype.Component

@Component
class ExecutedFileConverter(
    val executedFileStatusConverter: ExecutedFileStatusConverter,
) {
    fun convert(dto: ExecutedFileDto): ExecutedFile =
        ExecutedFile(
            id = dto.id,
            file = dto.file,
            drops = dto.drops,
            size = dto.size,
            recordedAt = dto.recordedAt,
            channel = dto.channel,
            title = dto.title,
            channelName = dto.channelName,
            duration = dto.duration,
            status = executedFileStatusConverter.convert(dto.status),
        )
}
