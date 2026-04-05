package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.external.database.dto.ProgramDto

class ProgramStatusConverterTest : ExpectSpec({
    val programStatusConverter = ProgramStatusConverter()

    context("convert") {
        listOf(
            ProgramDto.Status.REGISTERED to Program.Status.REGISTERED,
            ProgramDto.Status.COMPLETED to Program.Status.COMPLETED,
            ProgramDto.Status.ERROR to Program.Status.ERROR,
        ).forEach { (input, expected) ->
            expect("$input -> $expected") {
                programStatusConverter.convert(input) shouldBe expected
            }
        }
    }
})
