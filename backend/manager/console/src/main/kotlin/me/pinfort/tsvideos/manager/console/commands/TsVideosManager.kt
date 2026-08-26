package me.pinfort.tsvideos.manager.console.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import org.springframework.stereotype.Component

@Component
class TsVideosManager(
    private val search: Search,
    private val get: Get,
    private val delete: Delete,
    private val modify: Modify,
) : CliktCommand(name = "tvmcli") {
    init {
        subcommands(search, get, delete, modify)
    }

    override fun run() = Unit
}
