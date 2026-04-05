package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.external.database.dto.CreatedFileDto

class CreatedFileConverterTest : ExpectSpec({
    val createdFileStatusConverter = mockk<CreatedFileStatusConverter>()
    val createdFileConverter = CreatedFileConverter(createdFileStatusConverter)

    context("convert") {
        expect("success") {
            every { createdFileStatusConverter.convert(any()) } returns CreatedFile.Status.REGISTERED

            val actual =
                createdFileConverter.convert(
                    CreatedFileDto(
                        id = 1,
                        splittedFileId = 2,
                        file = "file",
                        size = 3,
                        mime = "mime",
                        encoding = "encoding",
                        status = CreatedFileDto.Status.REGISTERED,
                    ),
                )

            actual shouldBe
                CreatedFile(
                    id = 1,
                    splittedFileId = 2,
                    file = "file",
                    size = 3,
                    mime = "mime",
                    encoding = "encoding",
                    status = CreatedFile.Status.REGISTERED,
                )
        }
    }
})
