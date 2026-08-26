package me.pinfort.tsvideos.core.external.tool

import me.pinfort.tsvideos.core.config.ProcessorToolConfigurationProperties
import me.pinfort.tsvideos.core.external.shell.ShellComponent
import org.springframework.stereotype.Component
import java.io.File

@Component
class DropChkClient(
    private val shellComponent: ShellComponent,
    private val processorToolConfigurationProperties: ProcessorToolConfigurationProperties,
) {
    // the exit code of tsDropChk is the number of dropped frames, not a success/failure indicator
    fun check(
        file: File,
        timeoutSec: Long = 600,
    ): Int =
        shellComponent.executeOnWindows(
            file.parentFile,
            listOf(processorToolConfigurationProperties.tsDropChkPath, "-nolog", "-srcpath", file.absolutePath),
            timeoutSec,
        )
}
