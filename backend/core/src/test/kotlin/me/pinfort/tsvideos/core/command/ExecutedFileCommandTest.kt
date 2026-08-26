package me.pinfort.tsvideos.core.command

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.external.database.dto.ExecutedFileDto
import me.pinfort.tsvideos.core.external.database.dto.converter.ExecutedFileConverter
import me.pinfort.tsvideos.core.external.database.mapper.ExecutedFileMapper
import me.pinfort.tsvideos.core.external.database.mapper.GeneratedKeyHolder
import org.slf4j.Logger
import java.time.LocalDateTime

class ExecutedFileCommandTest :
    ExpectSpec({
        lateinit var executedFileMapper: ExecutedFileMapper
        lateinit var executedFileConverter: ExecutedFileConverter
        lateinit var logger: Logger
        lateinit var executedFileCommand: ExecutedFileCommand

        val executedFileDto =
            ExecutedFileDto(
                id = 1,
                file = "file",
                drops = 2,
                size = 3,
                recordedAt = LocalDateTime.MIN,
                channel = "channel",
                title = "title",
                channelName = "channelName",
                duration = 4.0,
                status = ExecutedFileDto.Status.SPLITTED,
            )

        val executedFile =
            ExecutedFile(
                id = 1,
                file = "file",
                drops = 2,
                size = 3,
                recordedAt = LocalDateTime.MIN,
                channel = "channel",
                title = "title",
                channelName = "channelName",
                duration = 4.0,
                status = ExecutedFile.Status.SPLITTED,
            )

        beforeTest {
            clearAllMocks()
            executedFileMapper = mockk()
            executedFileConverter = mockk()
            logger = mockk()
            executedFileCommand = ExecutedFileCommand(executedFileMapper, executedFileConverter, logger)
        }

        context("find") {
            expect("success") {
                every { executedFileMapper.find(any()) } returns executedFileDto
                every { executedFileConverter.convert(any()) } returns executedFile

                executedFileCommand.find(1) shouldBe executedFile

                verifySequence {
                    executedFileMapper.find(1)
                    executedFileConverter.convert(executedFileDto)
                }
            }

            expect("isNull") {
                every { executedFileMapper.find(any()) } returns null

                executedFileCommand.find(1) shouldBe null

                verifySequence {
                    executedFileMapper.find(1)
                }
                verify(exactly = 0) {
                    executedFileConverter.convert(any())
                }
            }
        }

        context("findByFile") {
            expect("found") {
                every { executedFileMapper.selectByFile(any()) } returns listOf(executedFileDto)
                every { executedFileConverter.convert(any()) } returns executedFile

                executedFileCommand.findByFile("file") shouldBe executedFile

                verifySequence {
                    executedFileMapper.selectByFile("file")
                    executedFileConverter.convert(executedFileDto)
                }
            }

            expect("none") {
                every { executedFileMapper.selectByFile(any()) } returns emptyList()

                executedFileCommand.findByFile("file") shouldBe null

                verifySequence {
                    executedFileMapper.selectByFile("file")
                }
                verify(exactly = 0) { executedFileConverter.convert(any()) }
            }
        }

        context("insert") {
            expect("success") {
                every {
                    executedFileMapper.insert(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
                } answers {
                    (it.invocation.args[9] as GeneratedKeyHolder).id = 1
                    1
                }
                every { logger.info(any()) } just Runs

                val actual =
                    executedFileCommand.insert(
                        file = "file",
                        drops = 2,
                        size = 3,
                        recordedAt = LocalDateTime.MIN,
                        channel = "channel",
                        title = "title",
                        channelName = "channelName",
                        duration = 4.0,
                    )

                actual shouldBe executedFile.copy(id = 1, status = ExecutedFile.Status.DROPCHECKED)
                verify {
                    executedFileMapper.insert(
                        "file",
                        2,
                        3,
                        LocalDateTime.MIN,
                        "channel",
                        "title",
                        "channelName",
                        4.0,
                        "DROPCHECKED",
                        any(),
                    )
                }
            }

            expect("dryRun") {
                every { logger.info(any()) } just Runs

                val actual =
                    executedFileCommand.insert(
                        file = "file",
                        drops = 2,
                        size = 3,
                        recordedAt = LocalDateTime.MIN,
                        channel = "channel",
                        title = "title",
                        channelName = "channelName",
                        duration = 4.0,
                        dryRun = true,
                    )

                actual shouldBe executedFile.copy(id = 0, status = ExecutedFile.Status.DROPCHECKED)
                verify(exactly = 0) {
                    executedFileMapper.insert(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
                }
            }
        }

        context("updateStatus") {
            expect("success") {
                every { executedFileMapper.updateStatus(any(), any()) } returns 1
                every { logger.info(any()) } just Runs

                val actual = executedFileCommand.updateStatus(executedFile, ExecutedFile.Status.SPLITTED)

                actual shouldBe executedFile.copy(status = ExecutedFile.Status.SPLITTED)
                verifySequence {
                    executedFileMapper.updateStatus(1, "SPLITTED")
                    logger.info(any())
                }
            }

            expect("dryRun") {
                every { logger.info(any()) } just Runs

                val actual = executedFileCommand.updateStatus(executedFile, ExecutedFile.Status.SPLITTED, true)

                actual shouldBe executedFile.copy(status = ExecutedFile.Status.SPLITTED)
                verify(exactly = 0) { executedFileMapper.updateStatus(any(), any()) }
            }
        }

        context("delete") {
            expect("success") {
                every { executedFileMapper.delete(any()) } just Runs
                every { logger.info(any()) } just Runs

                executedFileCommand.delete(executedFile)

                verifySequence {
                    executedFileMapper.delete(1)
                    logger.info(any())
                }
            }

            expect("dryRun") {
                every { logger.info(any()) } just Runs

                executedFileCommand.delete(executedFile, true)

                verifySequence {
                    logger.info(any())
                }
                verify(exactly = 0) {
                    executedFileMapper.delete(1)
                }
            }
        }
    })
