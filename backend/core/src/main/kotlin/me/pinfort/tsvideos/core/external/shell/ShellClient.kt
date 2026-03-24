package me.pinfort.tsvideos.core.external.shell

import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.TimeUnit

@Component
class ShellClient {
    fun execute(workingDir: File, command: String, timeoutSec: Long = 600): Int {
        val parts = command.split("\\s".toRegex())
        val exitCode: Int = kotlin.run {
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
            proc.waitFor(timeoutSec, TimeUnit.SECONDS)
            proc.exitValue()
        }
        return exitCode
    }
}
