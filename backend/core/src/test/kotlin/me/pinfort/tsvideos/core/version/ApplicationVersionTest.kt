package me.pinfort.tsvideos.core.version

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotBeBlank

class ApplicationVersionTest :
    ExpectSpec({
        context("version") {
            expect("ビルド時に生成されたversion.propertiesから読み出せる") {
                ApplicationVersion.version.shouldNotBeBlank()
                ApplicationVersion.version shouldNotBe "unknown"
            }
        }

        context("commit") {
            expect("ビルド時のgitコミットハッシュを読み出せる") {
                ApplicationVersion.commit shouldMatch Regex("[0-9a-f]{7,40}")
            }
        }

        context("value") {
            expect("バージョンとコミットハッシュを並べた文字列になる") {
                ApplicationVersion.value shouldBe "${ApplicationVersion.version} (${ApplicationVersion.commit})"
            }
        }
    })
