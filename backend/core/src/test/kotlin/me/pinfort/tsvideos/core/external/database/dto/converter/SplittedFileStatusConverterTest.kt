package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.database.dto.SplittedFileDto

class SplittedFileStatusConverterTest :
    ExpectSpec({
        val splittedFileStatusConverter = SplittedFileStatusConverter()

        context("convert") {
            listOf(
                SplittedFileDto.Status.REGISTERED to SplittedFile.Status.REGISTERED,
                SplittedFileDto.Status.COMPRESS_SAVED to SplittedFile.Status.COMPRESS_SAVED,
                SplittedFileDto.Status.ENCODE_TASK_ADDED to SplittedFile.Status.ENCODE_TASK_ADDED,
            ).forEach { (input, expected) ->
                expect("$input -> $expected") {
                    splittedFileStatusConverter.convert(input) shouldBe expected
                }
            }
        }
    })
