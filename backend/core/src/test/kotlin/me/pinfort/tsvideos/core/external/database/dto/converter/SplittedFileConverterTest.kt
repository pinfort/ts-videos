package me.pinfort.tsvideos.core.external.database.dto.converter

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.database.dto.SplittedFileDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SplittedFileConverterTest {
    @MockK
    private lateinit var convertStatus: SplittedFileStatusConverter

    @InjectMockKs
    private lateinit var splittedFileConverter: SplittedFileConverter

    private val dummySplittedFileDto =
        SplittedFileDto(
            id = 1,
            executedFileId = 2,
            file = "filepath",
            size = 3,
            duration = 4.0,
            status = SplittedFileDto.Status.REGISTERED,
        )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Nested
    inner class ConvertTest {
        @Test
        fun success() {
            every { convertStatus.convert(any()) } returns SplittedFile.Status.REGISTERED

            val actual = splittedFileConverter.convert(dummySplittedFileDto)

            val expected =
                SplittedFile(
                    id = 1,
                    executedFileId = 2,
                    file = "filepath",
                    size = 3,
                    duration = 4.0,
                    status = SplittedFile.Status.REGISTERED,
                )

            Assertions.assertThat(actual).isEqualTo(expected)
        }
    }
}
