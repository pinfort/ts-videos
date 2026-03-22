package me.pinfort.tsvideos.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "samba")
data class SambaConfigurationProperties(
    val videoStoreNas: Server,
    val originalStoreNas: Server,
) {
    data class Server(
        val url: String,
        val username: String,
        val password: String,
    )
}
