package me.pinfort.tsvideos.core.external.samba

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import me.pinfort.tsvideos.core.config.SambaConfigurationProperties

class SambaClientTest :
    ExpectSpec({
        fun sambaClient(
            baseDir: String,
            url: String = "smb://localhost/video-store/",
        ): SambaClient {
            val server =
                SambaConfigurationProperties.Server(
                    url = url,
                    username = "user",
                    password = "password",
                    baseDir = baseDir,
                )
            return SambaClient(
                SambaConfigurationProperties(
                    videoStoreNas = server,
                    originalStoreNas = server,
                ),
            )
        }

        context("videoStoreNas") {
            expect("returns the share root when baseDir is blank") {
                sambaClient("").videoStoreNas().locator.path shouldBe "smb://localhost/video-store/"
            }

            expect("resolves baseDir under the share root") {
                sambaClient("foo").videoStoreNas().locator.path shouldBe "smb://localhost/video-store/foo/"
            }

            expect("resolves a nested baseDir") {
                sambaClient("foo/bar").videoStoreNas().locator.path shouldBe "smb://localhost/video-store/foo/bar/"
            }

            expect("trims leading and trailing slashes from baseDir") {
                sambaClient("/foo/bar/").videoStoreNas().locator.path shouldBe "smb://localhost/video-store/foo/bar/"
            }

            expect("resolves baseDir under the share root when the configured url has no trailing slash") {
                sambaClient("foo", url = "smb://localhost:139/alice").videoStoreNas().locator.path shouldBe
                    "smb://localhost:139/alice/foo/"
            }
        }

        context("originalStoreNas") {
            expect("resolves baseDir under the share root") {
                sambaClient("baz").originalStoreNas().locator.path shouldBe "smb://localhost/video-store/baz/"
            }
        }
    })
