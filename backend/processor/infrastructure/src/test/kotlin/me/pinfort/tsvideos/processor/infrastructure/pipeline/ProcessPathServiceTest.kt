package me.pinfort.tsvideos.processor.infrastructure.pipeline

import io.kotest.core.spec.style.ExpectSpec
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import me.pinfort.tsvideos.core.command.ProcessFileCommand
import me.pinfort.tsvideos.processor.infrastructure.external.slack.SlackClient
import org.slf4j.Logger
import java.io.File
import java.nio.file.Files

class ProcessPathServiceTest :
    ExpectSpec({
        lateinit var processFileCommand: ProcessFileCommand
        lateinit var slackClient: SlackClient
        lateinit var logger: Logger
        lateinit var processPathService: ProcessPathService

        beforeTest {
            clearAllMocks()
            processFileCommand = mockk()
            slackClient = mockk()
            logger = mockk(relaxed = true)
            processPathService = ProcessPathService(processFileCommand, slackClient, logger)
        }

        context("processPath") {
            expect("notifies via Slack and does nothing when the path does not exist") {
                every { slackClient.notify(any()) } just Runs

                processPathService.processPath(File("/nonexistent/path").toPath())

                verify { slackClient.notify(any()) }
                verify(exactly = 0) { processFileCommand.processFile(any(), any(), any(), any()) }
            }

            expect("processes a single file path") {
                val file = File.createTempFile("process-path-test", ".m2ts")
                every { processFileCommand.processFile(any(), any(), any(), any()) } returns ProcessFileCommand.Result.PROCESSED

                processPathService.processPath(file.toPath())

                verify { processFileCommand.processFile(file, false, any(), any()) }
            }

            expect("processes every .m2ts file in a directory, ignoring other extensions") {
                val dir = Files.createTempDirectory("process-path-test").toFile()
                val m2ts1 = File(dir, "a.m2ts").apply { writeBytes(byteArrayOf(1)) }
                val m2ts2 = File(dir, "b.m2ts").apply { writeBytes(byteArrayOf(1)) }
                File(dir, "c.txt").writeBytes(byteArrayOf(1))
                every { processFileCommand.processFile(any(), any(), any(), any()) } returns ProcessFileCommand.Result.PROCESSED

                processPathService.processPath(dir.toPath())

                verify { processFileCommand.processFile(m2ts1, false, any(), any()) }
                verify { processFileCommand.processFile(m2ts2, false, any(), any()) }
                verify(exactly = 2) { processFileCommand.processFile(any(), any(), any(), any()) }
            }

            expect("notifies via Slack and continues to the next file when processing fails") {
                val dir = Files.createTempDirectory("process-path-test").toFile()
                val m2ts1 = File(dir, "a.m2ts").apply { writeBytes(byteArrayOf(1)) }
                val m2ts2 = File(dir, "b.m2ts").apply { writeBytes(byteArrayOf(1)) }
                every { processFileCommand.processFile(m2ts1, false, any(), any()) } throws RuntimeException("boom")
                every { processFileCommand.processFile(m2ts2, false, any(), any()) } returns ProcessFileCommand.Result.PROCESSED
                every { slackClient.notify(any()) } just Runs

                processPathService.processPath(dir.toPath())

                verify { slackClient.notify(any()) }
                verify { processFileCommand.processFile(m2ts2, false, any(), any()) }
            }

            expect("threads dryRun through to processFile") {
                val file = File.createTempFile("process-path-test", ".m2ts")
                every { processFileCommand.processFile(any(), any(), any(), any()) } returns ProcessFileCommand.Result.PROCESSED

                processPathService.processPath(file.toPath(), dryRun = true)

                verify { processFileCommand.processFile(file, true, any(), any()) }
            }
        }
    })
