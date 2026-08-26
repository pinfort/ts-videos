package me.pinfort.tsvideos.core.external.tool

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.pinfort.tsvideos.core.config.ProcessorToolConfigurationProperties
import me.pinfort.tsvideos.core.external.shell.ShellComponent
import java.io.File

class DropChkClientTest :
    ExpectSpec({
        val shellComponent = mockk<ShellComponent>()
        val properties =
            ProcessorToolConfigurationProperties(
                tsDropChkPath = "C:\\tools\\tsDropChkx64.exe",
                tsSplitterPath = "C:\\tools\\TsSplitter.exe",
                amatsukazeAddTaskPath = "C:\\tools\\AmatsukazeAddTask.exe",
                ffprobePath = "ffprobe",
                amatsukaze =
                    ProcessorToolConfigurationProperties.Amatsukaze(
                        host = "localhost",
                        port = 32768,
                        defaultProfile = "30fps_light",
                        atxDivProfile = "30fps_light_atx_div",
                    ),
            )
        val dropChkClient = DropChkClient(shellComponent, properties)

        context("check") {
            expect("builds the expected argv and returns the exit code") {
                val file = File("/recordings/[210708-0030][BSBS13_1][channel]title with spaces.m2ts")
                every { shellComponent.executeOnWindows(any(), any(), any()) } returns 3

                dropChkClient.check(file, 60) shouldBe 3

                verify {
                    shellComponent.executeOnWindows(
                        file.parentFile,
                        listOf("C:\\tools\\tsDropChkx64.exe", "-nolog", "-srcpath", file.absolutePath),
                        60,
                    )
                }
            }
        }
    })
