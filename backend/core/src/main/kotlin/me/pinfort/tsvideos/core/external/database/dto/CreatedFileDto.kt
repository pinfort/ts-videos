package me.pinfort.tsvideos.core.external.database.dto

data class CreatedFileDto(
    val id: Long,
    val splittedFileId: Long,
    val file: String,
    val size: Long,
    val mime: String?,
    val encoding: String?,
    val status: Status,
) {
    enum class Status {
        REGISTERED,
        ENCODE_SUCCESS,
        FILE_MOVED,
    }
}
