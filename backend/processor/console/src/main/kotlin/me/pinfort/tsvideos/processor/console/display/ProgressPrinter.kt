package me.pinfort.tsvideos.processor.console.display

class ProgressPrinter(
    private val label: String,
) {
    private var lastRenderedPercent: Int? = null
    private var lastTotalBytes: Long? = null

    fun render(
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
        print("\r$label [$bar] $percent%")
        System.out.flush()
        if (percent >= 100) println()
    }

    private companion object {
        const val PROGRESS_BAR_WIDTH = 30
    }
}
