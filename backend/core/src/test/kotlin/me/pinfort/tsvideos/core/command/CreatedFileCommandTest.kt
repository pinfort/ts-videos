package me.pinfort.tsvideos.core.command

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import jcifs.smb.SmbException
import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.external.database.dto.CreatedFileDto
import me.pinfort.tsvideos.core.external.database.dto.converter.CreatedFileConverter
import me.pinfort.tsvideos.core.external.database.mapper.CreatedFileMapper
import me.pinfort.tsvideos.core.external.samba.NasComponent
import me.pinfort.tsvideos.core.external.samba.SambaClient
import org.slf4j.Logger
import java.io.InputStream

class CreatedFileCommandTest : ExpectSpec({
    lateinit var createdFileMapper: CreatedFileMapper
    lateinit var createdFileConverter: CreatedFileConverter
    lateinit var sambaClient: SambaClient
    lateinit var nasComponent: NasComponent
    lateinit var logger: Logger
    lateinit var createdFileCommand: CreatedFileCommand

    val createdFileDto =
        CreatedFileDto(
            id = 1,
            splittedFileId = 2,
            file = "file",
            size = 3,
            mime = "mime",
            encoding = "encoding",
            status = CreatedFileDto.Status.ENCODE_SUCCESS,
        )
    val createdFile =
        CreatedFile(
            id = 1,
            splittedFileId = 2,
            file = "file",
            size = 3,
            mime = "mime",
            encoding = "encoding",
            status = CreatedFile.Status.ENCODE_SUCCESS,
        )

    beforeTest {
        createdFileMapper = mockk()
        createdFileConverter = mockk()
        sambaClient = mockk()
        nasComponent = mockk()
        logger = mockk()
        createdFileCommand = CreatedFileCommand(createdFileMapper, createdFileConverter, sambaClient, nasComponent, logger)
    }

    context("findMp4File") {
        expect("success") {
            val testCreatedFile = createdFile.copy(mime = "video/mp4")
            every { createdFileMapper.find(any()) } returns createdFileDto
            every { createdFileConverter.convert(any()) } returns testCreatedFile

            createdFileCommand.findMp4File(1) shouldBe testCreatedFile

            verifySequence {
                createdFileMapper.find(1)
                createdFileConverter.convert(createdFileDto)
                testCreatedFile.isMp4
            }
        }

        expect("notVideo") {
            every { createdFileMapper.find(any()) } returns createdFileDto
            every { createdFileConverter.convert(any()) } returns createdFile

            createdFileCommand.findMp4File(1) shouldBe null

            verifySequence {
                createdFileMapper.find(1)
                createdFileConverter.convert(createdFileDto)
                createdFile.isMp4
            }
        }

        expect("noHit") {
            every { createdFileMapper.find(any()) } returns null

            createdFileCommand.findMp4File(1) shouldBe null

            verifySequence {
                createdFileMapper.find(1)
            }
        }
    }

    context("streamCreatedFile") {
        expect("success") {
            val testCreatedFile = createdFile.copy(mime = "video/mp4")
            val testStream = InputStream.nullInputStream()
            every { createdFileMapper.find(any()) } returns createdFileDto
            every { createdFileConverter.convert(any()) } returns testCreatedFile
            every { sambaClient.videoStoreNas().resolve(any()).openInputStream() } returns testStream

            createdFileCommand.streamCreatedFile(1) shouldBe testStream

            verifySequence {
                createdFileMapper.find(1)
                createdFileConverter.convert(createdFileDto)
                sambaClient.videoStoreNas().resolve("file")
            }
        }

        expect("successBackSlash") {
            val testCreatedFile = createdFile.copy(mime = "video/mp4", file = "test\\")
            val testStream = InputStream.nullInputStream()
            every { createdFileMapper.find(any()) } returns createdFileDto
            every { createdFileConverter.convert(any()) } returns testCreatedFile
            every { sambaClient.videoStoreNas().resolve(any()).openInputStream() } returns testStream

            createdFileCommand.streamCreatedFile(1) shouldBe testStream

            verifySequence {
                createdFileMapper.find(1)
                createdFileConverter.convert(createdFileDto)
                sambaClient.videoStoreNas().resolve("test/")
            }
        }

        expect("noFile") {
            val testCreatedFile = createdFile.copy(mime = "video/mp4", file = "test\\")
            every { createdFileMapper.find(any()) } returns createdFileDto
            every { createdFileConverter.convert(any()) } returns testCreatedFile
            every { sambaClient.videoStoreNas().resolve(any()).openInputStream() } throws SmbException("err")

            createdFileCommand.streamCreatedFile(1) shouldBe null

            verifySequence {
                createdFileMapper.find(1)
                createdFileConverter.convert(createdFileDto)
                sambaClient.videoStoreNas().resolve("test/")
            }
        }

        expect("noHit") {
            every { createdFileMapper.find(any()) } returns null

            createdFileCommand.streamCreatedFile(1) shouldBe null

            verifySequence {
                createdFileMapper.find(1)
            }
        }
    }

    context("delete") {
        expect("success") {
            every { createdFileMapper.delete(any()) } just Runs
            every { nasComponent.deleteResource(createdFile.file) } returns SambaClient.NasType.ORIGINAL_STORE_NAS
            every { logger.info(any()) } just Runs

            createdFileCommand.delete(createdFile) shouldBe SambaClient.NasType.ORIGINAL_STORE_NAS

            verifySequence {
                createdFileMapper.delete(1)
                nasComponent.deleteResource(createdFile.file)
                logger.info(any())
            }
        }

        expect("noHit") {
            every { createdFileMapper.delete(any()) } just Runs
            every { nasComponent.deleteResource(any()) } throws Exception("err")
            every { logger.error(any(), any<Exception>()) } just Runs

            shouldThrow<RuntimeException> {
                createdFileCommand.delete(createdFile)
            }.message shouldBe "java.lang.Exception: err"

            verifySequence {
                createdFileMapper.delete(1)
                nasComponent.deleteResource(createdFile.file)
                logger.error("Failed to delete file. id=1, file=${createdFile.file}, createdFile=$createdFile", any<Exception>())
            }
        }

        expect("dryRun") {
            every { logger.info(any()) } just Runs

            createdFileCommand.delete(createdFile, true) shouldBe SambaClient.NasType.VIDEO_STORE_NAS

            verifySequence {
                logger.info(any())
            }
            verify(exactly = 0) {
                createdFileMapper.delete(any())
                nasComponent.deleteResource(any())
            }
        }
    }

    context("move") {
        expect("success") {
            every { createdFileMapper.updateFile(any(), any()) } returns 1
            every { nasComponent.moveResource(createdFile.file, "newFile") } returns SambaClient.NasType.ORIGINAL_STORE_NAS
            every { logger.info(any()) } just Runs

            createdFileCommand.move(createdFile, "newFile") shouldBe SambaClient.NasType.ORIGINAL_STORE_NAS

            verifySequence {
                createdFileMapper.updateFile(1, "newFile")
                nasComponent.moveResource(createdFile.file, "newFile")
                logger.info(any())
            }
        }

        expect("noHit") {
            every { createdFileMapper.updateFile(any(), any()) } returns 1
            every { nasComponent.moveResource(any(), any()) } throws Exception("err")
            every { logger.error(any(), any<Exception>()) } just Runs

            shouldThrow<RuntimeException> {
                createdFileCommand.move(createdFile, "newFile")
            }.message shouldBe "java.lang.Exception: err"

            verifySequence {
                createdFileMapper.updateFile(1, "newFile")
                nasComponent.moveResource(createdFile.file, "newFile")
                logger.error(
                    "Failed to move file. id=1, file=${createdFile.file}, newFile=newFile, createdFile=$createdFile",
                    any<Exception>(),
                )
            }
        }

        expect("dryRun") {
            every { logger.info(any()) } just Runs

            createdFileCommand.move(createdFile, "newFile", true) shouldBe SambaClient.NasType.VIDEO_STORE_NAS

            verifySequence {
                logger.info(any())
            }
            verify(exactly = 0) {
                createdFileMapper.updateFile(any(), any())
                nasComponent.moveResource(any(), any())
            }
        }
    }
})
