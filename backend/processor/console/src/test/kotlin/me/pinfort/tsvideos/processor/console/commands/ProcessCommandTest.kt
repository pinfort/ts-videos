package me.pinfort.tsvideos.processor.console.commands

import com.github.ajalt.clikt.core.main
import io.kotest.core.spec.style.ExpectSpec
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import me.pinfort.tsvideos.processor.infrastructure.pipeline.ProcessPathService
import java.nio.file.Path

class ProcessCommandTest :
    ExpectSpec({
        lateinit var processPathService: ProcessPathService
        lateinit var processCommand: ProcessCommand

        beforeTest {
            clearAllMocks()
            processPathService = mockk()
            processCommand = ProcessCommand(processPathService)
        }

        context("run") {
            expect("processes every positional path argument") {
                every { processPathService.processPath(any(), any(), any()) } just Runs

                processCommand.main(arrayOf("/path/one", "/path/two"))

                verify { processPathService.processPath(Path.of("/path/one"), false, any()) }
                verify { processPathService.processPath(Path.of("/path/two"), false, any()) }
            }

            expect("threads the dry-run flag through") {
                every { processPathService.processPath(any(), any(), any()) } just Runs

                processCommand.main(arrayOf("--dry-run", "/path/one"))

                verify { processPathService.processPath(Path.of("/path/one"), true, any()) }
            }
        }
    })
