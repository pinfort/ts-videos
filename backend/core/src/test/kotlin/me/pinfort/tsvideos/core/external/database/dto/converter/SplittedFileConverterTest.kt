package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.database.dto.SplittedFileDto

class SplittedFileConverterTest : ExpectSpec({
    val splittedFileStatusConverter = mockk<SplittedFileStatusConverter>()
    val splittedFileConverter = SplittedFileConverter(splittedFileStatusConverter)

    val dummySplittedFileDto =
        SplittedFileDto(
            id = 1,
            executedFileId = 2,
            file = "filepath",
            size = 3,
            duration = 4.0,
            status = SplittedFileDto.Status.REGISTERED,
        )

    context("convert") {
        expect("success") {
            every { splittedFileStatusConverter.convert(any()) } returns SplittedFile.Status.REGISTERED

            val actual = splittedFileConverter.convert(dummySplittedFileDto)

            actual shouldBe
                SplittedFile(
                    id = 1,
                    executedFileId = 2,
                    file = "filepath",
                    size = 3,
                    duration = 4.0,
                    status = SplittedFile.Status.REGISTERED,
                )
        }
    }
})
