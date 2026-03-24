package me.pinfort.tsvideos.core.external.samba

import jcifs.SmbResource
import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class NasComponent(
    private val sambaClient: SambaClient,
    private val logger: Logger,
) {
    private val videoStoreNas = sambaClient.videoStoreNas()
    private val originalStoreNas = sambaClient.originalStoreNas()

    fun getResource(file: String): SmbResource {
        // replace because windows path using '\'
        val resource = videoStoreNas.resolve(file.replace('\\', '/'))
        if (resource.exists()) return resource
        val originalResource = originalStoreNas.resolve(file.replace('\\', '/'))
        if (originalResource.exists()) return originalResource
        throw Exception("File not found, path=$file")
    }

    fun deleteResource(file: String): SambaClient.NasType {
        val resource = videoStoreNas.resolve(file.replace('\\', '/'))
        if (resource.exists()) {
            resource.delete()
            return SambaClient.NasType.VIDEO_STORE_NAS
        }
        val originalResource = originalStoreNas.resolve(file.replace('\\', '/'))
        if (originalResource.exists()) {
            originalResource.delete()
            return SambaClient.NasType.ORIGINAL_STORE_NAS
        }
        throw Exception("File not found, path=$file")
    }

    fun moveResource(
        oldFile: String,
        newFile: String,
    ): SambaClient.NasType {
        val resource = videoStoreNas.resolve(oldFile.replace('\\', '/'))
        if (resource.exists()) {
            createDirectories(newFile, SambaClient.NasType.VIDEO_STORE_NAS)
            resource.copyTo(videoStoreNas.resolve(newFile.replace('\\', '/')))
            resource.delete()
            logger.info("Move file, oldFile=$oldFile, newFile=$newFile")
            return SambaClient.NasType.VIDEO_STORE_NAS
        }
        val originalResource = originalStoreNas.resolve(oldFile.replace('\\', '/'))
        if (originalResource.exists()) {
            createDirectories(newFile, SambaClient.NasType.ORIGINAL_STORE_NAS)
            originalResource.copyTo(originalStoreNas.resolve(newFile.replace('\\', '/')))
            originalResource.delete()
            logger.info("Move file, oldFile=$oldFile, newFile=$newFile")
            return SambaClient.NasType.ORIGINAL_STORE_NAS
        }
        throw Exception("File not found, path=$oldFile")
    }

    fun createDirectories(
        file: String,
        nasType: SambaClient.NasType,
    ) {
        val programDirectory = Path.of(file.replace('\\', '/')).parent
        val indexDirectory = programDirectory.parent

        createDirectory(indexDirectory.toString(), nasType)
        createDirectory(programDirectory.toString(), nasType)
    }

    fun createDirectory(
        directory: String,
        nasType: SambaClient.NasType,
    ) {
        when (nasType) {
            SambaClient.NasType.VIDEO_STORE_NAS -> {
                val resource = videoStoreNas.resolve(directory)
                if (!resource.exists()) {
                    logger.info("Create directory, directory=$directory")
                    resource.mkdir()
                }
            }
            SambaClient.NasType.ORIGINAL_STORE_NAS -> {
                val resource = originalStoreNas.resolve(directory)
                if (!resource.exists()) {
                    logger.info("Create directory, directory=$directory")
                    resource.mkdir()
                }
            }
        }
    }
}
