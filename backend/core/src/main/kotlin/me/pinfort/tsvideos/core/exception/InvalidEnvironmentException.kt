package me.pinfort.tsvideos.core.exception

/**
 * the exception for unexpected environment
 * e.g. the function for only Windows was executed on Linux.
 */
class InvalidEnvironmentException(message: String) : TsVideosException(message)
