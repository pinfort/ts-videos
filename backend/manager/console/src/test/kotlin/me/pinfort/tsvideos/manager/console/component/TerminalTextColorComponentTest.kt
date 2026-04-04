package me.pinfort.tsvideos.manager.console.component

import io.kotest.core.spec.style.ExpectSpec
import org.assertj.core.api.Assertions

class TerminalTextColorComponentTest :
    ExpectSpec({
        val terminalTextColorComponent = TerminalTextColorComponent()

        context("warn") {
            expect("success") {
                val actual = terminalTextColorComponent.warn("test")

                val expected = "\u001B[${TerminalTextColorComponent.COLOR.YELLOW.code + 10}mtest\u001B[0m"
                Assertions.assertThat(actual).isEqualTo(expected)
            }
        }

        context("debug") {
            expect("success") {
                val actual = terminalTextColorComponent.debug("test")

                val expected = "\u001B[${TerminalTextColorComponent.COLOR.WHITE.code + 10}mtest\u001B[0m"
                Assertions.assertThat(actual).isEqualTo(expected)
            }
        }

        context("info") {
            expect("success") {
                val actual = terminalTextColorComponent.info("test")

                val expected = "\u001B[${TerminalTextColorComponent.COLOR.GREEN.code + 10}mtest\u001B[0m"
                Assertions.assertThat(actual).isEqualTo(expected)
            }
        }

        context("error") {
            expect("success") {
                val actual = terminalTextColorComponent.error("test")

                val expected = "\u001B[${TerminalTextColorComponent.COLOR.RED.code + 10}mtest\u001B[0m"
                Assertions.assertThat(actual).isEqualTo(expected)
            }
        }
    })
