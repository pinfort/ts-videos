package me.pinfort.tsvideos.manager.api.controller

import me.pinfort.tsvideos.core.command.CreatedFileCommand
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class VideoFileController(
    private val createdFileCommand: CreatedFileCommand,
) {
    @GetMapping("/api/v1/video/{id}/stream")
    fun stream(
        @PathVariable(name = "id") id: Long,
        @RequestHeader headers: HttpHeaders,
    ): ResponseEntity<ResourceRegion> {
        val video = createdFileCommand.streamCreatedFile(id) ?: return ResponseEntity.notFound().build()
        val contentType = MediaType.asMediaType(MimeType.valueOf("video/mp4"))
        val contentLength = video.contentLength()

        val (status, region) =
            try {
                val range = headers.range.firstOrNull()
                if (range == null) {
                    HttpStatus.OK to ResourceRegion(video, 0, contentLength)
                } else {
                    HttpStatus.PARTIAL_CONTENT to range.toResourceRegion(video)
                }
            } catch (e: IllegalArgumentException) {
                return ResponseEntity
                    .status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header(HttpHeaders.CONTENT_RANGE, "bytes */$contentLength")
                    .build()
            }

        return ResponseEntity
            .status(status)
            .header(HttpHeaders.ACCEPT_RANGES, "bytes")
            .contentType(contentType)
            .body(region)
    }
}
