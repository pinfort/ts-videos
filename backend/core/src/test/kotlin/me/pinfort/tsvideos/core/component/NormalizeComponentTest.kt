package me.pinfort.tsvideos.core.component

import io.kotest.core.spec.style.Test
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested

class NormalizeComponentTest {
    @InjectMockKs
    private lateinit var normalizeComponent: NormalizeComponent

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Nested
    inner class NormalizeTest {
        @Test
        fun success() {
            val actual = normalizeComponent.normalize("\\/:*?\"<>|~‼０１２３４５６７８９ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ")
            Assertions.assertThat(actual).isEqualTo("￥／：＊？”＜＞｜～!!0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
        }
    }

    @Nested
    inner class NormalizeSpecialCharactersTest {
        @Test
        fun success() {
            val actual = normalizeComponent.normalizeSpecialCharacters("\\/:*?\"<>|~‼")
            Assertions.assertThat(actual).isEqualTo("￥／：＊？”＜＞｜～!!")
        }
    }

    @Nested
    inner class NormalizeAlphaNumericTest {
        @Test
        fun success() {
            val actual = normalizeComponent.normalizeAlphaNumeric("０１２３４５６７８９ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ")
            Assertions.assertThat(actual).isEqualTo("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
        }
    }
}
