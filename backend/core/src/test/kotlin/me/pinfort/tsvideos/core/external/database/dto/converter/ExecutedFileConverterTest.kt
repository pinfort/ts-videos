package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.external.database.dto.ExecutedFileDto
import java.time.LocalDateTime

class ExecutedFileConverterTest :
    ExpectSpec({
        val executedFileStatusConverter = mockk<ExecutedFileStatusConverter>()
        val executedFileConverter = ExecutedFileConverter(executedFileStatusConverter)

        context("convert") {
            expect("success") {
                every { executedFileStatusConverter.convert(any()) } returns ExecutedFile.Status.REGISTERED

                val actual =
                    executedFileConverter.convert(
                        ExecutedFileDto(
                            id = 1,
                            file = "2",
                            drops = 3,
                            size = 4L,
                            recordedAt = LocalDateTime.MIN,
                            channel = "5",
                            title = "6",
                            channelName = "7",
                            duration = 8.0,
                            status = ExecutedFileDto.Status.REGISTERED,
                        ),
                    )

                actual shouldBe
                    ExecutedFile(
                        id = 1,
                        file = "2",
                        drops = 3,
                        size = 4L,
                        recordedAt = LocalDateTime.MIN,
                        channel = "5",
                        title = "6",
                        channelName = "7",
                        duration = 8.0,
                        status = ExecutedFile.Status.REGISTERED,
                    )
            }
        }
    })
