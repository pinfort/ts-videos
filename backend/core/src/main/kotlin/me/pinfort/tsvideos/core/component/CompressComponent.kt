package me.pinfort.tsvideos.core.component

import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.io.File
import java.util.zip.GZIPOutputStream

@Component
class CompressComponent(
    private val logger: Logger,
) {
    companion object {
        private const val COMPRESS_BUFFER_SIZE = 8 * 1024
        private val NO_OP_PROGRESS_LISTENER: (Long, Long) -> Unit = { _, _ -> }
    }

    // returns false (and logs, without throwing) if compressedFile already exists and force is not set
    fun compress(
        originalFile: File,
        compressedFile: File,
        force: Boolean = false,
    ): Boolean = compress(originalFile, compressedFile, force, NO_OP_PROGRESS_LISTENER)

    fun compress(
        originalFile: File,
        compressedFile: File,
        force: Boolean,
        onProgress: (bytesTransferred: Long, totalBytes: Long) -> Unit,
    ): Boolean {
        if (compressedFile.exists() && !force) {
            logger.error("Compressed file already exists, compressedFile=$compressedFile")
            return false
        }
        val totalBytes = originalFile.length()
        GZIPOutputStream(compressedFile.outputStream()).use { target ->
            originalFile.inputStream().buffered().use { input ->
                val buffer = ByteArray(COMPRESS_BUFFER_SIZE)
                var transferred = 0L
                onProgress(transferred, totalBytes)
                while (true) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    target.write(buffer, 0, read)
                    transferred += read
                    onProgress(transferred, totalBytes)
                }
            }
        }
        return true
    }
}
