package me.pinfort.tsvideos.core.external.database.dto.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.external.database.dto.ProgramDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ProgramStatusConverterTest {
    @InjectMockKs
    private lateinit var programStatusConverter: ProgramStatusConverter

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @CsvSource(
        "REGISTERED,REGISTERED",
        "COMPLETED,COMPLETED",
        "ERROR,ERROR"
    )
    @ParameterizedTest
    fun success(originalStatus: ProgramDto.Status, status: Program.Status) {
        val actual = programStatusConverter.convert(originalStatus)

        Assertions.assertThat(actual).isEqualTo(status)
    }
}
