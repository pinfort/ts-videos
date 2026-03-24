package me.pinfort.tsvideos.core.domain

import io.kotest.core.spec.style.Test
import me.pinfort.tsvideos.core.exception.InvalidFileNameException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import java.time.LocalDateTime

class FileNameTest {
    @Nested
    inner class FromFileNameStringTest {
        @Test
        fun success() {
            val actual = FileName.fromFileNameString("[230102-0405][GR99][てすと]テスト番組[SV:KT][ID:9999].m2ts")

            Assertions.assertThat(actual).isEqualTo(
                FileName(
                    recordedAt = LocalDateTime.of(2023, 1, 2, 4, 5, 0),
                    channel = "GR99",
                    channelName = "てすと",
                    title = "テスト番組[SV:KT][ID:9999].m2ts"
                )
            )
        }

        @Test
        fun successMinimum() {
            val actual = FileName.fromFileNameString("[230102-0405][GR99][てすと]テ.m2ts")

            Assertions.assertThat(actual).isEqualTo(
                FileName(
                    recordedAt = LocalDateTime.of(2023, 1, 2, 4, 5, 0),
                    channel = "GR99",
                    channelName = "てすと",
                    title = "テ.m2ts"
                )
            )
        }

        @Test
        fun failed() {
            Assertions.assertThatThrownBy {
                FileName.fromFileNameString("[230102-0405]テ.m2ts")
            }
                .isInstanceOf(InvalidFileNameException::class.java)
                .hasMessage("filename parsing error")
        }
    }

    @Nested
    inner class ToFileNameStringTest {
        @Test
        fun success() {
            val actual = FileName(
                recordedAt = LocalDateTime.of(2023, 3, 4, 5, 6, 0),
                channel = "BSBS_01",
                channelName = "てすと2",
                title = "テスト番組2[SV:YH][ID:9999].m2ts"
            ).toFileNameString()

            Assertions.assertThat(actual).isEqualTo(
                "[230304-0506][BSBS_01][てすと2]テスト番組2[SV:YH][ID:9999].m2ts"
            )
        }
    }
}
