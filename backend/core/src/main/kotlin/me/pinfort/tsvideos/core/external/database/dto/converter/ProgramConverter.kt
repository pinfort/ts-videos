package me.pinfort.tsvideos.core.external.database.dto.converter

import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.external.database.dto.ProgramDto
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ProgramConverter(
    private val programStatusConverter: ProgramStatusConverter
) {
    fun convert(dto: ProgramDto): Program {
        return Program(
            id = dto.id,
            name = dto.name,
            executedFileId = dto.executedFileId,
            status = programStatusConverter.convert(dto.status),
            drops = dto.drops ?: -1,
            size = dto.size ?: 0,
            recordedAt = dto.recordedAt ?: LocalDateTime.MIN,
            channel = dto.channel ?: "",
            title = dto.title ?: "",
            channelName = dto.channelName ?: "",
            duration = dto.duration ?: -1.0
        )
    }
}
