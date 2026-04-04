package me.pinfort.tsvideos.manager.console.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.enum
import me.pinfort.tsvideos.core.command.ExecutedFileCommand
import me.pinfort.tsvideos.core.command.ProgramCommand
import me.pinfort.tsvideos.manager.console.component.ProgramDetailToTextComponent
import org.springframework.stereotype.Component

@Component
class Get(
    private val programCommand: ProgramCommand,
    private val executedFileCommand: ExecutedFileCommand,
    private val programDetailToTextComponent: ProgramDetailToTextComponent
) : CliktCommand() {
    override fun help(context: Context): String = "get resources"

    private val resourceType: ResourceType by argument("resourceType", "type of target resource").enum<ResourceType>()
    private val id by argument("id", "id of resource")

    enum class ResourceType {
        PROGRAM,
        EXECUTED_FILE
    }

    override fun run() {
        when (resourceType) {
            ResourceType.PROGRAM -> {
                val program = programCommand.findDetail(id.toLong()) ?: return println("program not found")
                println(programDetailToTextComponent.convertConsole(program))
            }
            ResourceType.EXECUTED_FILE -> {
                val executedFile = executedFileCommand.find(id.toLong()) ?: return println("executedFile not found")
                println(executedFile)
            }
        }
    }
}
