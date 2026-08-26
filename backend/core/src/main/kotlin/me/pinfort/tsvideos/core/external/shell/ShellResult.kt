package me.pinfort.tsvideos.core.external.shell

data class ShellResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
)
