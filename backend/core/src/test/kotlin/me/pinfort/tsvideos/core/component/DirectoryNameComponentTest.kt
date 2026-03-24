package me.pinfort.tsvideos.core.component

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path

class DirectoryNameComponentTest {
    @MockK
    private lateinit var normalizeComponent: NormalizeComponent

    @InjectMockKs
    private lateinit var directoryNameComponent: DirectoryNameComponent

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Nested
    inner class IndexDirectoryName {
        @Test
        fun success() {
            every { normalizeComponent.normalize("かきくけこ") } returns "さしすせそ"

            val result = directoryNameComponent.indexDirectoryName(Path.of("あいうえお/かきくけこ/てすとvideofile.m2ts"))

            Assertions.assertThat(result).isEqualTo("さ")
        }
    }

    @Nested
    inner class ProgramDirectoryName {
        @Test
        fun success() {
            every { normalizeComponent.normalize("かきくけこ") } returns "さしすせそ"

            val result = directoryNameComponent.programDirectoryName(Path.of("あいうえお/かきくけこ/てすとvideofile.m2ts"))

            Assertions.assertThat(result).isEqualTo("さしすせそ")
        }
    }

    @Nested
    inner class ReplaceWithGivenDirectoryName {
        @Test
        fun success() {
            every { normalizeComponent.normalize("ほげ") } returns "ほげ"

            val result = directoryNameComponent.replaceWithGivenDirectoryName(Path.of("あいうえお/かきくけこ/さしすせそ/てすとvideofile.m2ts"), "ほげ")

            Assertions.assertThat(result).isEqualTo(Path.of("あいうえお/ほ/ほげ/てすとvideofile.m2ts"))
        }
    }
}
