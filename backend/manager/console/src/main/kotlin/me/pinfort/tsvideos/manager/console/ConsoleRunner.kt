package me.pinfort.tsvideos.manager.console

import com.github.ajalt.clikt.core.main
import me.pinfort.tsvideos.manager.console.commands.TsVideosManager
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ConsoleRunner(
//    private val search: Search,
//    private val get: Get,
//    private val delete: Delete,
//    private val modify: Modify
    private val tsVideosManager: TsVideosManager,
) : CommandLineRunner {
    override fun run(vararg args: String) = tsVideosManager.main(args)
}
