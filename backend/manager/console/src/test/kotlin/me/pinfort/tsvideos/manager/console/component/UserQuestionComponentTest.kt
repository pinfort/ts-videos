package me.pinfort.tsvideos.manager.console.component

import io.kotest.core.spec.style.ExpectSpec
import io.mockk.every
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions

class UserQuestionComponentTest : ExpectSpec({
    val userQuestionComponent = UserQuestionComponent()

    beforeEach {
        mockkStatic(::readlnOrNull)
    }

    context("askDefaultFalse") {
        expect("y returns true") {
            every { readlnOrNull() } returns "y"
            Assertions.assertThat(userQuestionComponent.askDefaultFalse("question")).isTrue()
        }

        expect("Y returns true") {
            every { readlnOrNull() } returns "Y"
            Assertions.assertThat(userQuestionComponent.askDefaultFalse("question")).isTrue()
        }

        expect("n returns false") {
            every { readlnOrNull() } returns "n"
            Assertions.assertThat(userQuestionComponent.askDefaultFalse("question")).isFalse()
        }

        expect("N returns false") {
            every { readlnOrNull() } returns "N"
            Assertions.assertThat(userQuestionComponent.askDefaultFalse("question")).isFalse()
        }

        expect("null returns false") {
            every { readlnOrNull() } returns null
            Assertions.assertThat(userQuestionComponent.askDefaultFalse("question")).isFalse()
        }

        expect("foo returns false") {
            every { readlnOrNull() } returns "foo"
            Assertions.assertThat(userQuestionComponent.askDefaultFalse("question")).isFalse()
        }
    }

    context("askDefaultTrue") {
        expect("y returns true") {
            every { readlnOrNull() } returns "y"
            Assertions.assertThat(userQuestionComponent.askDefaultTrue("question")).isTrue()
        }

        expect("Y returns true") {
            every { readlnOrNull() } returns "Y"
            Assertions.assertThat(userQuestionComponent.askDefaultTrue("question")).isTrue()
        }

        expect("n returns false") {
            every { readlnOrNull() } returns "n"
            Assertions.assertThat(userQuestionComponent.askDefaultTrue("question")).isFalse()
        }

        expect("N returns false") {
            every { readlnOrNull() } returns "N"
            Assertions.assertThat(userQuestionComponent.askDefaultTrue("question")).isFalse()
        }

        expect("null returns true") {
            every { readlnOrNull() } returns null
            Assertions.assertThat(userQuestionComponent.askDefaultTrue("question")).isTrue()
        }

        expect("foo returns true") {
            every { readlnOrNull() } returns "foo"
            Assertions.assertThat(userQuestionComponent.askDefaultTrue("question")).isTrue()
        }
    }
})
