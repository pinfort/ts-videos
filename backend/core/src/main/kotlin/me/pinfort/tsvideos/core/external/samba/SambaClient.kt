package me.pinfort.tsvideos.core.external.samba

import jcifs.CIFSContext
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

    fun videoStoreNas(): SmbFile {
        val context =
            cifsContext(
                sambaConfigurationProperties.videoStoreNas.username,
                sambaConfigurationProperties.videoStoreNas.password,
            )
        return connect(sambaConfigurationProperties.videoStoreNas.url, context)
    }

    fun originalStoreNas(): SmbFile {
        val context =
            cifsContext(
                sambaConfigurationProperties.originalStoreNas.username,
                sambaConfigurationProperties.originalStoreNas.password,
            )
        return connect(sambaConfigurationProperties.originalStoreNas.url, context)
    }

    private fun connect(
        url: String,
        context: CIFSContext,
    ): SmbFile = SmbFile(url, context)

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
