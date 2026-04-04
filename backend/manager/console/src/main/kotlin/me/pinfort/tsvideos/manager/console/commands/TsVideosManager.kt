package me.pinfort.tsvideos.manager.console.commands

import com.github.ajalt.clikt.core.CliktCommand
import org.springframework.stereotype.Component

@Component
class TsVideosManager(
    // TODO
) : CliktCommand(name = "tvmcli") {
    override fun run() = Unit
}
