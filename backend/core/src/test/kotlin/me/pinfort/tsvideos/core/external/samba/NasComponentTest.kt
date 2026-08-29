package me.pinfort.tsvideos.core.external.samba

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jcifs.SmbResource
import jcifs.smb.SmbFile
import org.slf4j.Logger
import java.io.ByteArrayOutputStream
import java.io.File

class NasComponentTest :
    ExpectSpec({
        lateinit var sambaClient: SambaClient
        lateinit var logger: Logger
        lateinit var videoStoreNas: SmbFile
        lateinit var originalStoreNas: SmbFile
        lateinit var nasComponent: NasComponent

        beforeTest {
            clearAllMocks()
            sambaClient = mockk()
            logger = mockk()
            videoStoreNas = mockk()
            originalStoreNas = mockk()
            every { sambaClient.videoStoreNas() } returns videoStoreNas
            every { sambaClient.originalStoreNas() } returns originalStoreNas
            nasComponent = NasComponent(sambaClient, logger)
        }

        context("resourceExists") {
            expect("true when the file is on the video store nas") {
                val resource = mockk<SmbResource>()
                every { videoStoreNas.resolve("a/bucket/file.mp4") } returns resource
                every { resource.exists() } returns true

                nasComponent.resourceExists("a\\bucket\\file.mp4") shouldBe true

                verify(exactly = 0) { originalStoreNas.resolve(any()) }
            }

            expect("true when the file is only on the original store nas") {
                val videoResource = mockk<SmbResource>()
                val originalResource = mockk<SmbResource>()
                every { videoStoreNas.resolve("a/bucket/file.gz") } returns videoResource
                every { videoResource.exists() } returns false
                every { originalStoreNas.resolve("a/bucket/file.gz") } returns originalResource
                every { originalResource.exists() } returns true

                nasComponent.resourceExists("a/bucket/file.gz") shouldBe true
            }

            expect("false when the file is on neither nas") {
                val videoResource = mockk<SmbResource>()
                val originalResource = mockk<SmbResource>()
                every { videoStoreNas.resolve("a/bucket/file.mp4") } returns videoResource
                every { videoResource.exists() } returns false
                every { originalStoreNas.resolve("a/bucket/file.mp4") } returns originalResource
                every { originalResource.exists() } returns false

                nasComponent.resourceExists("a/bucket/file.mp4") shouldBe false
            }
        }

        context("uploadResource") {
            expect("skip when target already exists") {
                val resource = mockk<SmbResource>()
                every { originalStoreNas.resolve(any()) } returns resource
                every { resource.exists() } returns true
                every { logger.info(any()) } just Runs

                val localFile = File.createTempFile("nas-upload-test", ".tmp")

                val result = nasComponent.uploadResource(localFile, "a/bucket/file.gz", SambaClient.NasType.ORIGINAL_STORE_NAS)

                result shouldBe false
                verify(exactly = 0) { resource.openOutputStream() }
                verify(exactly = 0) { resource.mkdir() }
            }

            expect("uploads when target does not exist") {
                val resource = mockk<SmbResource>()
                val output = ByteArrayOutputStream()
                every { originalStoreNas.resolve(any()) } returns resource
                every { resource.exists() } returns false
                every { resource.mkdir() } just Runs
                every { resource.openOutputStream() } returns output
                every { logger.info(any()) } just Runs

                val localFile = File.createTempFile("nas-upload-test", ".tmp")
                localFile.writeBytes(byteArrayOf(1, 2, 3))

                val result = nasComponent.uploadResource(localFile, "a/bucket/file.gz", SambaClient.NasType.ORIGINAL_STORE_NAS)

                result shouldBe true
                output.toByteArray() shouldBe byteArrayOf(1, 2, 3)
                verify { resource.mkdir() }
                verify { resource.openOutputStream() }
            }

            expect("uploads to video store nas") {
                val resource = mockk<SmbResource>()
                val output = ByteArrayOutputStream()
                every { videoStoreNas.resolve(any()) } returns resource
                every { resource.exists() } returns false
                every { resource.mkdir() } just Runs
                every { resource.openOutputStream() } returns output
                every { logger.info(any()) } just Runs

                val localFile = File.createTempFile("nas-upload-test", ".tmp")

                val result = nasComponent.uploadResource(localFile, "a/bucket/file.mp4", SambaClient.NasType.VIDEO_STORE_NAS)

                result shouldBe true
                verify { videoStoreNas.resolve("a/bucket/file.mp4") }
            }
        }
    })
