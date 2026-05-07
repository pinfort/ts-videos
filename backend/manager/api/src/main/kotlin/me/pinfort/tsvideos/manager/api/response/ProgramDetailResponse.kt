package me.pinfort.tsvideos.manager.api.response

import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.domain.ProgramDetail

data class ProgramDetailResponse(
    val program: ProgramDetail,
    val videoFiles: List<CreatedFile>,
)
