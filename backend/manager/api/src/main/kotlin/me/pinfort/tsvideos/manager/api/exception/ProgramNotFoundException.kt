package me.pinfort.tsvideos.manager.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(
    value = HttpStatus.NOT_FOUND,
    reason = "program not found.",
)
class ProgramNotFoundException(
    message: String,
) : RuntimeException(message)
