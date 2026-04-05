package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.external.database.dto.ProgramDto
import java.time.LocalDateTime

class ProgramConverterTest :
    ExpectSpec({
        val programStatusConverter = mockk<ProgramStatusConverter>()
        val programConverter = ProgramConverter(programStatusConverter)

        context("convert") {
            expect("success") {
                every { programStatusConverter.convert(any()) } returns Program.Status.COMPLETED

                val actual =
                    programConverter.convert(
                        ProgramDto(
                            id = 1,
                            name = "2",
                            executedFileId = 3,
                            status = ProgramDto.Status.COMPLETED,
                            drops = 4,
                            size = 5,
                            recordedAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                            channel = "channel",
                            title = "title",
                            channelName = "channelName",
                            duration = 6.0,
                        ),
                    )

                actual shouldBe
                    Program(
                        id = 1,
                        name = "2",
                        executedFileId = 3,
                        status = Program.Status.COMPLETED,
                        drops = 4,
                        size = 5,
                        recordedAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                        channel = "channel",
                        title = "title",
                        channelName = "channelName",
                        duration = 6.0,
                    )
            }
        }
    })
