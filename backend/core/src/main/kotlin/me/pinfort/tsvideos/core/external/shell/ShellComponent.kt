package me.pinfort.tsvideos.core.external.shell

import me.pinfort.tsvideos.core.component.RunningPlatformComponent
import me.pinfort.tsvideos.core.exception.InvalidEnvironmentException
import java.io.File

class ShellComponent(
    private val runningPlatformComponent: RunningPlatformComponent,
    private val shellClient: ShellClient
) {
    fun executeOnWindows(workingDir: File, commands: String, timeout: Long = 600): Int {
        if (!runningPlatformComponent.runningOnWindows()) {
            throw InvalidEnvironmentException("This function must be called on Windows only.")
        }

        return shellClient.execute(workingDir, commands, timeout)
    }
}
