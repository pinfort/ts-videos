package me.pinfort.tsvideos.core.command

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import me.pinfort.tsvideos.core.component.DirectoryNameComponent
import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.domain.ExecutedFile
import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.domain.ProgramDetail
import me.pinfort.tsvideos.core.domain.SplittedFile
import me.pinfort.tsvideos.core.external.database.dto.CreatedFileDto
import me.pinfort.tsvideos.core.external.database.dto.ProgramDto
import me.pinfort.tsvideos.core.external.database.dto.SplittedFileDto
import me.pinfort.tsvideos.core.external.database.dto.converter.CreatedFileConverter
import me.pinfort.tsvideos.core.external.database.dto.converter.ProgramConverter
import me.pinfort.tsvideos.core.external.database.dto.converter.ProgramDetailConverter
import me.pinfort.tsvideos.core.external.database.dto.converter.SplittedFileConverter
import me.pinfort.tsvideos.core.external.database.mapper.CreatedFileMapper
import me.pinfort.tsvideos.core.external.database.mapper.ProgramMapper
import me.pinfort.tsvideos.core.external.database.mapper.SplittedFileMapper
import me.pinfort.tsvideos.core.external.samba.SambaClient
import org.slf4j.Logger
import java.nio.file.Path
import java.time.LocalDateTime

class ProgramCommandTest :
    ExpectSpec({

        lateinit var programMapper: ProgramMapper
        lateinit var programConverter: ProgramConverter
        lateinit var createdFileMapper: CreatedFileMapper
        lateinit var createdFileConverter: CreatedFileConverter
        lateinit var programDetailConverter: ProgramDetailConverter
        lateinit var executedFileCommand: ExecutedFileCommand
        lateinit var createdFileCommand: CreatedFileCommand
        lateinit var splittedFileMapper: SplittedFileMapper
        lateinit var logger: Logger
        lateinit var splittedFileCommand: SplittedFileCommand
        lateinit var splittedFileConverter: SplittedFileConverter
        lateinit var directoryNameComponent: DirectoryNameComponent
        lateinit var programCommand: ProgramCommand

        beforeTest {
            clearAllMocks()

            programMapper = mockk<ProgramMapper>()
            programConverter = mockk<ProgramConverter>()
            createdFileMapper = mockk<CreatedFileMapper>()
            createdFileConverter = mockk<CreatedFileConverter>()
            programDetailConverter = mockk<ProgramDetailConverter>()
            executedFileCommand = mockk<ExecutedFileCommand>()
            createdFileCommand = mockk<CreatedFileCommand>()
            splittedFileMapper = mockk<SplittedFileMapper>()
            logger = mockk<Logger>()
            splittedFileCommand = mockk<SplittedFileCommand>()
            splittedFileConverter = mockk<SplittedFileConverter>()
            directoryNameComponent = mockk<DirectoryNameComponent>()
            programCommand =
                ProgramCommand(
                    programMapper,
                    programConverter,
                    createdFileMapper,
                    createdFileConverter,
                    programDetailConverter,
                    executedFileCommand,
                    createdFileCommand,
                    splittedFileMapper,
                    logger,
                    splittedFileCommand,
                    splittedFileConverter,
                    directoryNameComponent,
                )
        }

        val program =
            Program(
                id = 1,
                name = "name",
                executedFileId = 2,
                status = Program.Status.COMPLETED,
                drops = 3,
                size = 4,
                recordedAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                channel = "channel",
                title = "title",
                channelName = "channelName",
                duration = 5.0,
            )
        val programDto =
            ProgramDto(
                id = 1,
                name = "name",
                executedFileId = 2,
                status = ProgramDto.Status.COMPLETED,
                drops = 3,
                size = 4,
                recordedAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                channel = "channel",
                title = "title",
                channelName = "channelName",
                duration = 5.0,
            )
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
                file = "file\\file1",
                size = 3,
                mime = "mime",
                encoding = "encoding",
                status = CreatedFile.Status.ENCODE_SUCCESS,
            )
        val programDetail =
            ProgramDetail(
                id = 1,
                name = "name",
                executedFileId = 2,
                status = Program.Status.COMPLETED,
                drops = 3,
                size = 4,
                recordedAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                channel = "channel",
                title = "title",
                channelName = "channelName",
                duration = 5.0,
                createdFiles = listOf(createdFile),
            )
        val executedFile =
            ExecutedFile(
                id = 1,
                status = ExecutedFile.Status.REGISTERED,
                size = 2,
                recordedAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                channel = "channel",
                title = "title",
                channelName = "channelName",
                duration = 3.0,
                drops = 4,
                file = "file",
            )
        val splittedFileDto =
            SplittedFileDto(
                id = 1,
                executedFileId = 2,
                file = "file",
                size = 3,
                status = SplittedFileDto.Status.REGISTERED,
                duration = 4.0,
            )
        val splittedFile =
            SplittedFile(
                id = 1,
                executedFileId = 2,
                file = "file",
                size = 3,
                status = SplittedFile.Status.REGISTERED,
                duration = 4.0,
            )

        context("selectByName") {
            expect("success") {
                every { programMapper.selectByName(any(), any(), any()) } returns listOf(programDto)
                every { programConverter.convert(any()) } returns program

                val actual = programCommand.selectByName("test", 1, 2)

                actual shouldBe listOf(program)

                verifySequence {
                    programMapper.selectByName("test", 1, 2)
                    programConverter.convert(programDto)
                }
            }

            expect("noHit") {
                every { programMapper.selectByName(any(), any(), any()) } returns emptyList()

                val actual = programCommand.selectByName("test", 1, 2)

                actual shouldBe emptyList()

                verifySequence {
                    programMapper.selectByName("test", 1, 2)
                }
            }
        }

        context("find") {
            expect("success") {
                every { programMapper.find(any()) } returns programDto
                every { programConverter.convert(any()) } returns program

                val actual = programCommand.find(1)

                actual shouldBe program

                verifySequence {
                    programMapper.find(1)
                    programConverter.convert(programDto)
                }
            }

            expect("noHit") {
                every { programMapper.find(any()) } returns null

                val actual = programCommand.find(1)

                actual shouldBe null

                verifySequence {
                    programMapper.find(1)
                }
            }
        }

        context("videoFiles") {
            expect("success") {
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf(createdFileDto)
                every { createdFileConverter.convert(any()) } returns createdFile

                val testProgram = program.copy(executedFileId = 1)

                val actual = programCommand.videoFiles(testProgram)

                actual shouldBe listOf(createdFile)

                verifySequence {
                    createdFileMapper.selectByExecutedFileId(1)
                    createdFileConverter.convert(createdFileDto)
                }
            }

            expect("noHit") {
                every { createdFileMapper.selectByExecutedFileId(any()) } returns emptyList()

                val testProgram = program.copy(executedFileId = 1)

                val actual = programCommand.videoFiles(testProgram)

                actual shouldBe emptyList()

                verifySequence {
                    createdFileMapper.selectByExecutedFileId(1)
                }
            }
        }

        context("hasTsFile") {
            expect("success") {
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf(createdFileDto)
                every { createdFileConverter.convert(any()).isTs } returns true

                val actual = programCommand.hasTsFile(program)

                actual shouldBe true
            }

            expect("successNoTs") {
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf(createdFileDto)
                every { createdFileConverter.convert(any()).isTs } returns false

                val actual = programCommand.hasTsFile(program)

                actual shouldBe false
            }

            expect("successNoResult") {
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf()

                val actual = programCommand.hasTsFile(program)

                actual shouldBe false

                verify(exactly = 0) {
                    createdFileConverter.convert(any())
                }
            }
        }

        context("findDetail") {
            expect("success") {
                every { programMapper.find(any()) } returns programDto
                every { programDetailConverter.convert(any(), any()) } returns programDetail
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf(createdFileDto)

                val actual = programCommand.findDetail(1)

                actual shouldBe programDetail

                verifySequence {
                    programMapper.find(1)
                    programDetailConverter.convert(programDto, listOf(createdFileDto))
                }
            }

            expect("noHit") {
                every { programMapper.find(any()) } returns null

                val actual = programCommand.findDetail(1)

                actual shouldBe null

                verifySequence {
                    programMapper.find(1)
                }
            }
        }

        context("delete") {
            expect("success") {
                every { executedFileCommand.find(any()) } returns executedFile
                every { splittedFileMapper.selectByExecutedFileId(any()) } returns listOf(splittedFileDto)
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf(createdFileDto)
                every { splittedFileConverter.convert(any()) } returns splittedFile
                every { splittedFileCommand.delete(any()) } just Runs
                every { logger.info(any()) } just Runs
                every { createdFileCommand.delete(any()) } returns SambaClient.NasType.ORIGINAL_STORE_NAS
                every { createdFileConverter.convert(any()) } returns createdFile
                every { executedFileCommand.delete(any()) } just Runs
                every { programMapper.deleteById(any()) } just Runs

                programCommand.delete(program)

                verifySequence {
                    executedFileCommand.find(program.executedFileId)
                    splittedFileMapper.selectByExecutedFileId(executedFile.id)
                    createdFileMapper.selectByExecutedFileId(program.executedFileId)
                    splittedFileConverter.convert(splittedFileDto)
                    splittedFileCommand.delete(splittedFile, false)
                    createdFileConverter.convert(createdFileDto)
                    createdFileCommand.delete(createdFile, false)
                    executedFileCommand.delete(executedFile, false)
                    programMapper.deleteById(program.id)
                    logger.info(any())
                }
            }

            expect("executedNotFound") {
                every { executedFileCommand.find(any()) } throws Exception("not found")

                val ex =
                    shouldThrow<Exception> {
                        programCommand.delete(program)
                    }
                ex.message shouldBe "not found"

                verifySequence {
                    executedFileCommand.find(program.executedFileId)
                }
            }

            expect("dryRun") {
                every { executedFileCommand.find(any()) } returns executedFile
                every { splittedFileMapper.selectByExecutedFileId(any()) } returns listOf(splittedFileDto)
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf(createdFileDto)
                every { splittedFileConverter.convert(any()) } returns splittedFile
                every { splittedFileCommand.delete(any(), any()) } just Runs
                every { logger.info(any()) } just Runs
                every { createdFileCommand.delete(any(), any()) } returns SambaClient.NasType.ORIGINAL_STORE_NAS
                every { createdFileConverter.convert(any()) } returns createdFile
                every { executedFileCommand.delete(any(), any()) } just Runs
                every { programMapper.deleteById(any()) } just Runs

                programCommand.delete(program, true)

                verifySequence {
                    executedFileCommand.find(program.executedFileId)
                    splittedFileMapper.selectByExecutedFileId(executedFile.id)
                    createdFileMapper.selectByExecutedFileId(program.executedFileId)
                    splittedFileConverter.convert(splittedFileDto)
                    splittedFileCommand.delete(splittedFile, true)
                    createdFileConverter.convert(createdFileDto)
                    createdFileCommand.delete(createdFile, true)
                    executedFileCommand.delete(executedFile, true)
                    logger.info(any())
                }
                verify(exactly = 0) {
                    programMapper.deleteById(program.id)
                }
            }

            expect("exception") {
                every { executedFileCommand.find(any()) } returns executedFile
                every { splittedFileMapper.selectByExecutedFileId(any()) } returns listOf(splittedFileDto)
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf(createdFileDto)
                every { splittedFileConverter.convert(any()) } returns splittedFile
                every { splittedFileCommand.delete(any(), any()) } just Runs
                every { logger.info(any()) } just Runs
                every { createdFileCommand.delete(any(), any()) } throws RuntimeException("error")
                every { createdFileConverter.convert(any()) } returns createdFile
                every { executedFileCommand.delete(any(), any()) } just Runs
                every { programMapper.deleteById(any()) } just Runs

                shouldThrow<RuntimeException> {
                    programCommand.delete(program, true)
                }

                verifySequence {
                    executedFileCommand.find(program.executedFileId)
                    splittedFileMapper.selectByExecutedFileId(executedFile.id)
                    createdFileMapper.selectByExecutedFileId(program.executedFileId)
                    splittedFileConverter.convert(splittedFileDto)
                    splittedFileCommand.delete(splittedFile, true)
                    createdFileConverter.convert(createdFileDto)
                    createdFileCommand.delete(createdFile, true)
                }
                verify(exactly = 0) {
                    programMapper.deleteById(program.id)
                    executedFileCommand.delete(executedFile, true)
                    logger.info(any())
                }
            }
        }

        context("moveCreatedFiles") {
            expect("success") {
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf(createdFileDto)
                every { createdFileConverter.convert(any()) } returns createdFile
                every { createdFileCommand.move(any(), any()) } returns SambaClient.NasType.ORIGINAL_STORE_NAS
                every { logger.info(any()) } just Runs
                every { directoryNameComponent.replaceWithGivenDirectoryName(any(), any()) } returns Path.of("newPath")

                programCommand.moveCreatedFiles(program, "newDirectory")

                verifySequence {
                    createdFileMapper.selectByExecutedFileId(program.executedFileId)
                    createdFileConverter.convert(createdFileDto)
                    createdFileCommand.move(createdFile, "newPath")
                    logger.info(any())
                }
            }

            expect("dryRun") {
                every { createdFileMapper.selectByExecutedFileId(any()) } returns listOf(createdFileDto)
                every { createdFileConverter.convert(any()) } returns createdFile
                every { createdFileCommand.move(any(), any(), any()) } returns SambaClient.NasType.ORIGINAL_STORE_NAS
                every { logger.info(any()) } just Runs
                every { directoryNameComponent.replaceWithGivenDirectoryName(any(), any()) } returns Path.of("newPath")

                programCommand.moveCreatedFiles(program, "newDirectory", true)

                verifySequence {
                    createdFileMapper.selectByExecutedFileId(program.executedFileId)
                    createdFileConverter.convert(createdFileDto)
                    createdFileCommand.move(createdFile, "newPath", true)
                    logger.info(any())
                }
            }
        }
    })
