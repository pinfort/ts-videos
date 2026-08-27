package me.pinfort.tsvideos.processor.console.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import me.pinfort.tsvideos.processor.infrastructure.pipeline.ProcessPathService
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class ProcessCommand(
    private val processPathService: ProcessPathService,
) : CliktCommand(name = "tvpcli") {
    override fun help(context: Context): String = "process ts recording files (drop check -> split -> compress -> encode queue)"

    private val paths by argument("paths", help = "file or directory containing .m2ts files").multiple(required = true)
    private val dryRun by option("-d", "--dry-run").flag(default = false)

    override fun run() {
        paths.forEach { processPathService.processPath(Path.of(it), dryRun, ::printUploadProgress) }
    }

    private var lastRenderedPercent: Int? = null
    private var lastTotalBytes: Long? = null

    private fun printUploadProgress(
        bytesTransferred: Long,
        totalBytes: Long,
    ) {
        if (totalBytes <= 0) return
        if (totalBytes != lastTotalBytes) {
            lastTotalBytes = totalBytes
            lastRenderedPercent = null
        }
        val percent = (bytesTransferred * 100 / totalBytes).toInt().coerceIn(0, 100)
        if (percent == lastRenderedPercent) return
        lastRenderedPercent = percent
        val filled = PROGRESS_BAR_WIDTH * percent / 100
        val bar = "#".repeat(filled) + "-".repeat(PROGRESS_BAR_WIDTH - filled)
        print("\rUploading [$bar] $percent%")
        System.out.flush()
        if (percent >= 100) println()
    }

    private companion object {
        const val PROGRESS_BAR_WIDTH = 30
    }
}
