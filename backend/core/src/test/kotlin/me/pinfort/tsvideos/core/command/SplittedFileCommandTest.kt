package me.pinfort.tsvideos.core.command

import io.kotest.core.spec.style.ExpectSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.database.mapper.SplittedFileMapper
import org.slf4j.Logger

class SplittedFileCommandTest :
    ExpectSpec({
        lateinit var splittedFileMapper: SplittedFileMapper
        lateinit var logger: Logger
        lateinit var splittedFileCommand: SplittedFileCommand

        val splittedFile =
            SplittedFile(
                id = 1,
                executedFileId = 1,
                file = "test.ts",
                size = 1,
                duration = 1.0,
                status = SplittedFile.Status.REGISTERED,
            )

        beforeTest {
            splittedFileMapper = mockk()
            logger = mockk()
            splittedFileCommand = SplittedFileCommand(splittedFileMapper, logger)
        }

        context("delete") {
            expect("success") {
                every { splittedFileMapper.delete(any()) } just Runs
                every { logger.info(any()) } just Runs

                splittedFileCommand.delete(splittedFile)

                verifySequence {
                    splittedFileMapper.delete(splittedFile.id)
                    logger.info(
                        "Delete splitted file, id=1, splittedFile=SplittedFile(id=1, executedFileId=1, file=test.ts, size=1, duration=1.0, status=REGISTERED)",
                    )
                }
            }

            expect("dryRun") {
                every { logger.info(any()) } just Runs

                splittedFileCommand.delete(splittedFile, true)

                verifySequence {
                    logger.info(
                        "Delete splitted file, id=1, splittedFile=SplittedFile(id=1, executedFileId=1, file=test.ts, size=1, duration=1.0, status=REGISTERED)",
                    )
                }
                verify(exactly = 0) { splittedFileMapper.delete(any()) }
            }
        }
    })
