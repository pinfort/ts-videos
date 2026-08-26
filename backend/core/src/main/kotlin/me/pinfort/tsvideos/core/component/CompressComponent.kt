package me.pinfort.tsvideos.core.component

import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.io.File
import java.util.zip.GZIPOutputStream

@Component
class CompressComponent(
    private val logger: Logger,
) {
    // returns false (and logs, without throwing) if compressedFile already exists and force is not set
    fun compress(
        originalFile: File,
        compressedFile: File,
        force: Boolean = false,
    ): Boolean {
        if (compressedFile.exists() && !force) {
            logger.error("Compressed file already exists, compressedFile=$compressedFile")
            return false
        }
        GZIPOutputStream(compressedFile.outputStream()).use { target ->
            originalFile.inputStream().buffered().use { it.copyTo(target) }
        }
        return true
    }
}
