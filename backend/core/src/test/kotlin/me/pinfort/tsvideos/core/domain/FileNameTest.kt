package me.pinfort.tsvideos.core.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import me.pinfort.tsvideos.core.exception.InvalidFileNameException
import java.time.LocalDateTime

class FileNameTest :
    ExpectSpec({
        context("fromFileNameString") {
            expect("success") {
                val actual = FileName.fromFileNameString("[230102-0405][GR99][てすと]テスト番組[SV:KT][ID:9999].m2ts")

                actual shouldBe
                    FileName(
                        recordedAt = LocalDateTime.of(2023, 1, 2, 4, 5, 0),
                        channel = "GR99",
                        channelName = "てすと",
                        title = "テスト番組[SV:KT][ID:9999].m2ts",
                    )
            }

            expect("successMinimum") {
                val actual = FileName.fromFileNameString("[230102-0405][GR99][てすと]テ.m2ts")

                actual shouldBe
                    FileName(
                        recordedAt = LocalDateTime.of(2023, 1, 2, 4, 5, 0),
                        channel = "GR99",
                        channelName = "てすと",
                        title = "テ.m2ts",
                    )
            }

            expect("failed") {
                val ex =
                    shouldThrow<InvalidFileNameException> {
                        FileName.fromFileNameString("[230102-0405]テ.m2ts")
                    }
                ex.message shouldBe "filename parsing error"
            }
        }

        context("toFileNameString") {
            expect("success") {
                val actual =
                    FileName(
                        recordedAt = LocalDateTime.of(2023, 3, 4, 5, 6, 0),
                        channel = "BSBS_01",
                        channelName = "てすと2",
                        title = "テスト番組2[SV:YH][ID:9999].m2ts",
                    ).toFileNameString()

                actual shouldBe "[230304-0506][BSBS_01][てすと2]テスト番組2[SV:YH][ID:9999].m2ts"
            }
        }
    })
