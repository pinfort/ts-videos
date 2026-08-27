package me.pinfort.tsvideos.core.external.samba

import jcifs.CIFSContext
import jcifs.SmbResource
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import me.pinfort.tsvideos.core.config.SambaConfigurationProperties
import org.springframework.stereotype.Component
import java.util.Properties

@Component
class SambaClient(
    private val sambaConfigurationProperties: SambaConfigurationProperties,
) {
    enum class NasType {
        VIDEO_STORE_NAS,
        ORIGINAL_STORE_NAS,
    }

    fun videoStoreNas(): SmbResource {
        val context =
            cifsContext(
                sambaConfigurationProperties.videoStoreNas.username,
                sambaConfigurationProperties.videoStoreNas.password,
            )
        val root = connect(sambaConfigurationProperties.videoStoreNas.url, context)
        return resolveBaseDir(root, sambaConfigurationProperties.videoStoreNas.baseDir)
    }

    fun originalStoreNas(): SmbResource {
        val context =
            cifsContext(
                sambaConfigurationProperties.originalStoreNas.username,
                sambaConfigurationProperties.originalStoreNas.password,
            )
        val root = connect(sambaConfigurationProperties.originalStoreNas.url, context)
        return resolveBaseDir(root, sambaConfigurationProperties.originalStoreNas.baseDir)
    }

    private fun connect(
        url: String,
        context: CIFSContext,
    ): SmbFile = SmbFile(url, context)

    private fun resolveBaseDir(
        root: SmbFile,
        baseDir: String,
    ): SmbResource = if (baseDir.isBlank()) root else root.resolve(baseDir.trim('/') + "/")

    private fun cifsContext(
        username: String,
        password: String,
    ): CIFSContext {
        val auth =
            NtlmPasswordAuthenticator(
                username,
                password,
            )
        return baseContext.withCredentials(auth)
    }

    private val properties =
        Properties()
            .apply {
                setProperty("jcifs.smb.client.minVersion", "SMB202")
                setProperty("jcifs.smb.client.maxVersion", "SMB311")
            }

    private val baseContext =
        BaseContext(
            PropertyConfiguration(properties),
        )
}
