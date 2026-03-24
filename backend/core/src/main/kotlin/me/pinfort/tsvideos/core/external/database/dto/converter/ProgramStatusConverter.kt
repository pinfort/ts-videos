package me.pinfort.tsvideos.core.external.database.dto.converter

import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.external.database.dto.ProgramDto
import org.springframework.stereotype.Component

@Component
class ProgramStatusConverter {
    fun convert(dto: ProgramDto.Status): Program.Status =
        when (dto) {
            ProgramDto.Status.REGISTERED -> Program.Status.REGISTERED
            ProgramDto.Status.COMPLETED -> Program.Status.COMPLETED
            ProgramDto.Status.ERROR -> Program.Status.ERROR
        }
}
