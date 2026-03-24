package me.pinfort.tsvideos.core.domain

import java.time.LocalDateTime

data class ProgramDetail(
    val id: Long,
    val name: String,
    val executedFileId: Long,
    val status: Program.Status,
    val drops: Int,
    val size: Long,
    val recordedAt: LocalDateTime,
    val channel: String,
    val title: String,
    val channelName: String,
    val duration: Double,
    val createdFiles: List<CreatedFile>
)
