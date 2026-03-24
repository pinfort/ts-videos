package me.pinfort.tsvideos.core.external.database.dto

import java.time.LocalDateTime

data class ExecutedFileDto(
    val id: Long,
    val file: String,
    val drops: Int,
    val size: Long,
    val recordedAt: LocalDateTime,
    val channel: String,
    val title: String,
    val channelName: String,
    val duration: Double,
    val status: Status,
) {
    enum class Status {
        REGISTERED,
        DROPCHECKED,
        SPLITTED,
    }
}
