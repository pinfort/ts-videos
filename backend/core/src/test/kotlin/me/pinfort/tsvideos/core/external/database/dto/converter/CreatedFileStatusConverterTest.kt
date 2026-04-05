package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.external.database.dto.CreatedFileDto

class CreatedFileStatusConverterTest :
    ExpectSpec({
        val createdFileStatusConverter = CreatedFileStatusConverter()

        context("convert") {
            listOf(
                CreatedFileDto.Status.REGISTERED to CreatedFile.Status.REGISTERED,
                CreatedFileDto.Status.ENCODE_SUCCESS to CreatedFile.Status.ENCODE_SUCCESS,
                CreatedFileDto.Status.FILE_MOVED to CreatedFile.Status.FILE_MOVED,
            ).forEach { (input, expected) ->
                expect("$input -> $expected") {
                    createdFileStatusConverter.convert(input) shouldBe expected
                }
            }
        }
    })
