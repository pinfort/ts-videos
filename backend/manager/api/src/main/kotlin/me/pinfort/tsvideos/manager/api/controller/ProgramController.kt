package me.pinfort.tsvideos.manager.api.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import me.pinfort.tsvideos.core.command.ProgramCommand
import me.pinfort.tsvideos.manager.api.exception.ProgramNotFoundException
import me.pinfort.tsvideos.manager.api.response.ProgramDetailResponse
import me.pinfort.tsvideos.manager.api.response.SearchResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class ProgramController(
    private val programCommand: ProgramCommand,
) {
    @GetMapping("/api/v1/programs")
    fun index(
        @RequestParam(name = "name", required = false, defaultValue = "") name: String,
        @RequestParam(name = "limit", required = false, defaultValue = "100")
        @Max(100)
        @Positive
        limit: Int,
        @RequestParam(name = "offset", required = false, defaultValue = "0") @PositiveOrZero offset: Int,
    ): SearchResponse =
        SearchResponse(
            programCommand.selectByName(
                name,
                limit,
                offset,
            ),
        )

    @GetMapping("/api/v1/programs/{id}")
    fun get(
        @PathVariable(name = "id") id: Long,
    ): ProgramDetailResponse {
        val program = programCommand.find(id) ?: throw ProgramNotFoundException("program not found. id=$id")
        val programDetail = programCommand.findDetail(id) ?: throw ProgramNotFoundException("program not found. id=$id")
        val videoFiles = programCommand.videoFiles(program)
        return ProgramDetailResponse(
            program = programDetail,
            videoFiles = videoFiles,
        )
    }

    @DeleteMapping("/api/v1/programs/{id}")
    fun delete(
        @PathVariable(name = "id") id: Long,
    ): ResponseEntity<Unit> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
}
