package me.pinfort.tsvideos.core.external.database.dto

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import me.pinfort.tsvideos.core.domain.Program
import java.time.LocalDateTime

class ProgramDtoTest :
    ExpectSpec({
        context("toDomain") {
            expect("success with all fields") {
                val dto =
                    ProgramDto(
                        id = 1,
                        name = "name",
                        executedFileId = 2,
                        status = ProgramDto.Status.COMPLETED,
                        drops = 3,
                        size = 4,
                        recordedAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                        channel = "channel",
                        title = "title",
                        channelName = "channelName",
                        duration = 5.0,
                    )

                val actual = dto.toDomain()

                actual shouldBe
                    Program(
                        id = 1,
                        name = "name",
                        executedFileId = 2,
                        status = Program.Status.COMPLETED,
                        drops = 3,
                        size = 4,
                        recordedAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                        channel = "channel",
                        title = "title",
                        channelName = "channelName",
                        duration = 5.0,
                    )
            }

            expect("success with null values") {
                val dto =
                    ProgramDto(
                        id = 1,
                        name = "name",
                        executedFileId = 2,
                        status = ProgramDto.Status.REGISTERED,
                        drops = null,
                        size = null,
                        recordedAt = null,
                        channel = null,
                        title = null,
                        channelName = null,
                        duration = null,
                    )

                val actual = dto.toDomain()

                actual shouldBe
                    Program(
                        id = 1,
                        name = "name",
                        executedFileId = 2,
                        status = Program.Status.REGISTERED,
                        drops = -1,
                        size = 0,
                        recordedAt = LocalDateTime.MIN,
                        channel = "",
                        title = "",
                        channelName = "",
                        duration = -1.0,
                    )
            }

            expect("status REGISTERED") {
                val dto =
                    ProgramDto(
                        id = 1,
                        name = "name",
                        executedFileId = 2,
                        status = ProgramDto.Status.REGISTERED,
                        drops = null,
                        size = null,
                        recordedAt = null,
                        channel = null,
                        title = null,
                        channelName = null,
                        duration = null,
                    )

                dto.toDomain().status shouldBe Program.Status.REGISTERED
            }

            expect("status ERROR") {
                val dto =
                    ProgramDto(
                        id = 1,
                        name = "name",
                        executedFileId = 2,
                        status = ProgramDto.Status.ERROR,
                        drops = null,
                        size = null,
                        recordedAt = null,
                        channel = null,
                        title = null,
                        channelName = null,
                        duration = null,
                    )

                dto.toDomain().status shouldBe Program.Status.ERROR
            }
        }
    })
