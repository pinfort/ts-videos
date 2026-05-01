package me.pinfort.tsvideos.manager.api.controller

import me.pinfort.tsvideos.core.command.CreatedFileCommand
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.InputStream

@RestController
class VideoFileController(
    private val createdFileCommand: CreatedFileCommand,
) {
    @GetMapping("/api/v1/video/{id}/stream")
    fun stream(
        @PathVariable(name = "id") id: Long,
    ): ResponseEntity<InputStreamResource> {
        val responseHeaders = HttpHeaders()
        responseHeaders.contentType = MediaType.asMediaType(MimeType.valueOf("video/mp4"))
        val video =
            createdFileCommand.streamCreatedFile(id) ?: return ResponseEntity(
                InputStreamResource(InputStream.nullInputStream()),
                HttpStatus.NOT_FOUND,
            )
        return ResponseEntity(
            InputStreamResource(video.buffered()),
            responseHeaders,
            HttpStatus.OK,
        )
    }
}
