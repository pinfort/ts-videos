package me.pinfort.tsvideos.core.exception

class InvalidFileNameException(
    message: String?,
    cause: Throwable? = null,
) : TsVideosException(message, cause)
