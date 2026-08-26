package me.pinfort.tsvideos.processor.console

import com.github.ajalt.clikt.core.main
import me.pinfort.tsvideos.processor.console.commands.ProcessCommand
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ConsoleRunner(
    private val processCommand: ProcessCommand,
) : CommandLineRunner {
    override fun run(vararg args: String) = processCommand.main(args)
}
