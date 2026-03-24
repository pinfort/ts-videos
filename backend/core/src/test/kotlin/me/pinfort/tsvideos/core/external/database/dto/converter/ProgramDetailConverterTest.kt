package me.pinfort.tsvideos.core.external.database.dto.converter

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.domain.ProgramDetail
import me.pinfort.tsvideos.core.external.database.dto.CreatedFileDto
import me.pinfort.tsvideos.core.external.database.dto.ProgramDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProgramDetailConverterTest {
    @MockK
    private lateinit var programStatusConverter: ProgramStatusConverter

    @MockK
    private lateinit var createdFileConverter: CreatedFileConverter

    @InjectMockKs
    private lateinit var programDetailConverter: ProgramDetailConverter

    val dummyCreatedFileDto = CreatedFileDto(
        id = 1,
        splittedFileId = 2,
        file = "file",
        size = 3,
        mime = null,
        encoding = null,
        status = CreatedFileDto.Status.REGISTERED
    )

    val dummyCreatedFile = CreatedFile(
        id = 1,
        splittedFileId = 2,
        file = "file",
        size = 3,
        mime = null,
        encoding = null,
        status = CreatedFile.Status.REGISTERED
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun success() {
        every { programStatusConverter.convert(any()) } returns Program.Status.COMPLETED
        every { createdFileConverter.convert(any()) } returns dummyCreatedFile

        val actual = programDetailConverter.convert(
            ProgramDto(
                id = 1,
                name = "2",
                executedFileId = 3,
                status = ProgramDto.Status.COMPLETED,
                drops = 4,
                size = 5,
                recordedAt = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
                channel = "channel",
                title = "title",
                channelName = "channelName",
                duration = 6.0
            ),
            listOf(
                dummyCreatedFileDto
            )
        )

        val expected = ProgramDetail(
            id = 1,
            name = "2",
            executedFileId = 3,
            status = Program.Status.COMPLETED,
            drops = 4,
            size = 5,
            recordedAt = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
            channel = "channel",
            title = "title",
            channelName = "channelName",
            duration = 6.0,
            createdFiles = listOf(
                dummyCreatedFile
            )
        )

        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun successNull() {
        every { programStatusConverter.convert(any()) } returns Program.Status.COMPLETED
        every { createdFileConverter.convert(any()) } returns dummyCreatedFile

        val actual = programDetailConverter.convert(
            ProgramDto(
                id = 1,
                name = "2",
                executedFileId = 3,
                status = ProgramDto.Status.COMPLETED,
                drops = null,
                size = null,
                recordedAt = null,
                channel = null,
                title = null,
                channelName = null,
                duration = null
            ),
            listOf(
                dummyCreatedFileDto
            )
        )

        val expected = ProgramDetail(
            id = 1,
            name = "2",
            executedFileId = 3,
            status = Program.Status.COMPLETED,
            drops = -1,
            size = 0,
            recordedAt = LocalDateTime.MIN,
            channel = "",
            title = "",
            channelName = "",
            duration = -1.0,
            createdFiles = listOf(
                dummyCreatedFile
            )
        )

        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
