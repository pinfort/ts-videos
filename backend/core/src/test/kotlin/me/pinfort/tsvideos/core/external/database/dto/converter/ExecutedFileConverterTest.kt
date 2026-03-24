package me.pinfort.tsvideos.core.external.database.dto.converter

import io.kotest.core.spec.style.Test
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.external.database.dto.ExecutedFileDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime

class ExecutedFileConverterTest {
    @MockK
    private lateinit var executedFileStatusConverter: ExecutedFileStatusConverter

    @InjectMockKs
    private lateinit var executedFileConverter: ExecutedFileConverter

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun success() {
        every { executedFileStatusConverter.convert(any()) } returns ExecutedFile.Status.REGISTERED

        val actual = executedFileConverter.convert(
            ExecutedFileDto(
                id = 1,
                file = "2",
                drops = 3,
                size = 4L,
                recordedAt = LocalDateTime.MIN,
                channel = "5",
                title = "6",
                channelName = "7",
                duration = 8.0,
                status = ExecutedFileDto.Status.REGISTERED
            )
        )

        Assertions.assertThat(actual).isEqualTo(
            ExecutedFile(
                id = 1,
                file = "2",
                drops = 3,
                size = 4L,
                recordedAt = LocalDateTime.MIN,
                channel = "5",
                title = "6",
                channelName = "7",
                duration = 8.0,
                status = ExecutedFile.Status.REGISTERED
            )
        )
    }
}
