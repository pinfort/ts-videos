package me.pinfort.tsvideos.core.external.shell

import io.kotest.core.spec.style.Test
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import me.pinfort.tsvideos.core.component.RunningPlatformComponent
import me.pinfort.tsvideos.core.exception.InvalidEnvironmentException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import java.io.File

class ShellComponentTest {
    @MockK
    private lateinit var runningPlatformComponent: RunningPlatformComponent

    @MockK
    private lateinit var shellClient: ShellClient

    @InjectMockKs
    private lateinit var shellComponent: ShellComponent

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Nested
    inner class ExecuteOnWindowsTest {
        @Test
        fun success() {
            every { runningPlatformComponent.runningOnWindows() } returns true
            every { shellClient.execute(any(), any(), any()) } returns 1

            val actual = shellComponent.executeOnWindows(File("."), "ls", 10)

            Assertions.assertThat(actual).isEqualTo(1)
        }

        @Test
        fun failed() {
            every { runningPlatformComponent.runningOnWindows() } returns false

            Assertions
                .assertThatThrownBy {
                    shellComponent.executeOnWindows(File("."), "ls", 10)
                }.isInstanceOf(InvalidEnvironmentException::class.java)
                .hasMessage("This function must be called on Windows only.")
        }
    }
}
