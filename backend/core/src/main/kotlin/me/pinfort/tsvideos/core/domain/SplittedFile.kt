package me.pinfort.tsvideos.core.domain

data class SplittedFile(
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
