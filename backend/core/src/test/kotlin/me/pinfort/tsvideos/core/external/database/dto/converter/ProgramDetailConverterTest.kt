package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.domain.ProgramDetail
import me.pinfort.tsvideos.core.external.database.dto.CreatedFileDto
import me.pinfort.tsvideos.core.external.database.dto.ProgramDto
import java.time.LocalDateTime

class ProgramDetailConverterTest :
    ExpectSpec({
        val createdFileConverter = mockk<CreatedFileConverter>()
        val programDetailConverter = ProgramDetailConverter(createdFileConverter)

        val dummyCreatedFileDto =
            CreatedFileDto(
                id = 1,
                splittedFileId = 2,
                file = "file",
                size = 3,
                mime = null,
                encoding = null,
                status = CreatedFileDto.Status.REGISTERED,
            )

        val dummyCreatedFile =
            CreatedFile(
                id = 1,
                splittedFileId = 2,
                file = "file",
                size = 3,
                mime = null,
                encoding = null,
                status = CreatedFile.Status.REGISTERED,
            )

        context("convert") {
            expect("success") {
                every { createdFileConverter.convert(any()) } returns dummyCreatedFile

                val actual =
                    programDetailConverter.convert(
                        ProgramDto(
                            id = 1,
                            name = "2",
                            executedFileId = 3,
                            status = ProgramDto.Status.COMPLETED,
                            drops = 4,
                            size = 5,
                            recordedAt = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
                            channel = "channel",
                            title = "title",
                            channelName = "channelName",
                            duration = 6.0,
                        ),
                        listOf(dummyCreatedFileDto),
                    )

                actual shouldBe
                    ProgramDetail(
                        id = 1,
                        name = "2",
                        executedFileId = 3,
                        status = ProgramDetail.Status.COMPLETED,
                        drops = 4,
                        size = 5,
                        recordedAt = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
                        channel = "channel",
                        title = "title",
                        channelName = "channelName",
                        duration = 6.0,
                        createdFiles = listOf(dummyCreatedFile),
                    )
            }

            expect("success with null fields") {
                every { createdFileConverter.convert(any()) } returns dummyCreatedFile

                val actual =
                    programDetailConverter.convert(
                        ProgramDto(
                            id = 1,
                            name = "2",
                            executedFileId = 3,
                            status = ProgramDto.Status.COMPLETED,
                            drops = null,
                            size = null,
                            recordedAt = null,
                            channel = null,
                            title = null,
                            channelName = null,
                            duration = null,
                        ),
                        listOf(dummyCreatedFileDto),
                    )

                actual shouldBe
                    ProgramDetail(
                        id = 1,
                        name = "2",
                        executedFileId = 3,
                        status = ProgramDetail.Status.COMPLETED,
                        drops = -1,
                        size = 0,
                        recordedAt = LocalDateTime.MIN,
                        channel = "",
                        title = "",
                        channelName = "",
                        duration = -1.0,
                        createdFiles = listOf(dummyCreatedFile),
                    )
            }
        }
    })
