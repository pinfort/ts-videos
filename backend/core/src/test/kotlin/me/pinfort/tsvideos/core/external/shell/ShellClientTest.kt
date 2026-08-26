package me.pinfort.tsvideos.core.external.shell

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import me.pinfort.tsvideos.core.exception.ShellCommandTimeoutException
import java.io.File

class ShellClientTest :
    ExpectSpec({
        val shellClient = ShellClient()
        val workingDir = File(".")

        context("execute") {
            expect("success with an argument containing spaces") {
                val exitCode =
                    shellClient.execute(
                        workingDir,
                        listOf("bash", "-c", "test \"\$1\" = \"a value with spaces\"", "--", "a value with spaces"),
                        10,
                    )

                exitCode shouldBe 0
            }

            expect("returns the process exit code") {
                val exitCode = shellClient.execute(workingDir, listOf("bash", "-c", "exit 3"), 10)

                exitCode shouldBe 3
            }

            expect("timeout kills the process and throws") {
                shouldThrow<ShellCommandTimeoutException> {
                    shellClient.execute(workingDir, listOf("sleep", "5"), 1)
                }
            }
        }

        context("executeCapturingOutput") {
            expect("captures stdout and stderr separately") {
                val result =
                    shellClient.executeCapturingOutput(
                        workingDir,
                        listOf("bash", "-c", "echo out; echo err 1>&2"),
                        10,
                    )

                result.exitCode shouldBe 0
                result.stdout shouldBe "out\n"
                result.stderr shouldBe "err\n"
            }

            expect("does not deadlock on large output") {
                val result =
                    shellClient.executeCapturingOutput(
                        workingDir,
                        listOf("bash", "-c", "head -c 1000000 /dev/zero | tr '\\0' 'a'"),
                        10,
                    )

                result.exitCode shouldBe 0
                result.stdout.length shouldBe 1000000
            }

            expect("timeout kills the process and throws") {
                shouldThrow<ShellCommandTimeoutException> {
                    shellClient.executeCapturingOutput(workingDir, listOf("sleep", "5"), 1)
                }
            }
        }
    })
