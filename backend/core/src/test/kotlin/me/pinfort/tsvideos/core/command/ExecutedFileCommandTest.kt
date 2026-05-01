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
