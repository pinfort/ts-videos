package me.pinfort.tsvideos.core.external.shell

import me.pinfort.tsvideos.core.exception.ShellCommandTimeoutException
import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class ShellClient {
    fun execute(
        workingDir: File,
        command: List<String>,
        timeoutSec: Long = 600,
    ): Int {
        val proc =
            ProcessBuilder(command)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
        return awaitExitCode(proc, command, timeoutSec)
    }

    fun executeCapturingOutput(
        workingDir: File,
        command: List<String>,
        timeoutSec: Long = 600,
    ): ShellResult {
        val proc =
            ProcessBuilder(command)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        val drainExecutor = Executors.newFixedThreadPool(2)
        try {
            val stdoutFuture = drainExecutor.submit<String> { proc.inputStream.bufferedReader().readText() }
            val stderrFuture = drainExecutor.submit<String> { proc.errorStream.bufferedReader().readText() }

            val exitCode = awaitExitCode(proc, command, timeoutSec)

            return ShellResult(exitCode, stdoutFuture.get(), stderrFuture.get())
        } finally {
            drainExecutor.shutdown()
        }
    }

    private fun awaitExitCode(
        proc: Process,
        command: List<String>,
        timeoutSec: Long,
    ): Int {
        val finished = proc.waitFor(timeoutSec, TimeUnit.SECONDS)
        if (!finished) {
            proc.destroyForcibly()
            throw ShellCommandTimeoutException("Command timed out after ${timeoutSec}s, command=$command")
        }
        return proc.exitValue()
    }
}
