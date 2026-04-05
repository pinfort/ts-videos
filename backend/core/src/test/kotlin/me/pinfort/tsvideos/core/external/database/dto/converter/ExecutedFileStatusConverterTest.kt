package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.external.database.dto.ExecutedFileDto

class ExecutedFileStatusConverterTest : ExpectSpec({
    val executedFileStatusConverter = ExecutedFileStatusConverter()

    context("convert") {
        listOf(
            ExecutedFileDto.Status.REGISTERED to ExecutedFile.Status.REGISTERED,
            ExecutedFileDto.Status.DROPCHECKED to ExecutedFile.Status.DROPCHECKED,
            ExecutedFileDto.Status.SPLITTED to ExecutedFile.Status.SPLITTED,
        ).forEach { (input, expected) ->
            expect("$input -> $expected") {
                executedFileStatusConverter.convert(input) shouldBe expected
            }
        }
    }
})
