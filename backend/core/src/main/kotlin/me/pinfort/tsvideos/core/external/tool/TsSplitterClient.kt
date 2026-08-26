package me.pinfort.tsvideos.core.external.tool

import me.pinfort.tsvideos.core.config.ProcessorToolConfigurationProperties
import me.pinfort.tsvideos.core.external.shell.ShellComponent
import org.springframework.stereotype.Component
import java.io.File

@Component
class TsSplitterClient(
    private val shellComponent: ShellComponent,
    private val processorToolConfigurationProperties: ProcessorToolConfigurationProperties,
) {
    fun split(
        inFile: File,
        outDir: File,
        timeoutSec: Long,
    ): Int =
        shellComponent.executeOnWindows(
            inFile.parentFile,
            listOf(
                processorToolConfigurationProperties.tsSplitterPath,
                "-SD",
                "-EIT",
                "-1SEG",
                "-OUT",
                outDir.absolutePath,
                "-SEP",
                inFile.absolutePath,
            ),
            timeoutSec,
        )
}
