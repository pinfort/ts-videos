package me.pinfort.tsvideos.core.external.database.dto

import java.time.LocalDateTime

data class ProgramDto(
    val id: Long,
    val name: String,
    val executedFileId: Long,
    val status: Status,
    val drops: Int?,
    val size: Long?,
    val recordedAt: LocalDateTime?,
    val channel: String?,
    val title: String?,
    val channelName: String?,
    val duration: Double?
) {
    enum class Status {
        REGISTERED,
        COMPLETED,
        ERROR
    }
}
