package me.pinfort.tsvideos.core.external.database.dto

data class SplittedFileDto(
    val id: Long,
    val executedFileId: Long,
    val file: String,
    val size: Long,
    val duration: Double,
    val status: Status,
) {
    enum class Status {
        REGISTERED,
        COMPRESS_SAVED,
        ENCODE_TASK_ADDED,
    }
}
