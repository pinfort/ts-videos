package me.pinfort.tsvideos.core.external.database.dto.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.external.database.dto.ExecutedFileDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ExecutedFileStatusConverterTest {
    @InjectMockKs
    private lateinit var executedFileStatusConverter: ExecutedFileStatusConverter

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @CsvSource(
        "REGISTERED,REGISTERED",
        "DROPCHECKED,DROPCHECKED",
        "SPLITTED,SPLITTED",
    )
    @ParameterizedTest
    fun success(
        originalStatus: ExecutedFileDto.Status,
        status: ExecutedFile.Status,
    ) {
        val actual = executedFileStatusConverter.convert(originalStatus)

        Assertions.assertThat(actual).isEqualTo(status)
    }
}
