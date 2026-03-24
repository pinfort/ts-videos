package me.pinfort.tsvideos.core.external.database.dto.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.database.dto.SplittedFileDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SplittedFileStatusConverterTest {
    @InjectMockKs
    private lateinit var splittedFileStatusConverter: SplittedFileStatusConverter

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Nested
    inner class ConvertTest {
        @ParameterizedTest
        @CsvSource(
            "REGISTERED, REGISTERED",
            "COMPRESS_SAVED, COMPRESS_SAVED",
            "ENCODE_TASK_ADDED, ENCODE_TASK_ADDED",
        )
        fun success(
            status: SplittedFileDto.Status,
            expected: SplittedFile.Status,
        ) {
            val actual = splittedFileStatusConverter.convert(status)

            Assertions.assertThat(actual).isEqualTo(expected)
        }
    }
}
