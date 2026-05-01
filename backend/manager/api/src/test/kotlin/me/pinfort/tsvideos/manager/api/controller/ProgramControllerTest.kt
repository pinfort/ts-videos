package me.pinfort.tsvideos.manager.api.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import me.pinfort.tsvideos.core.command.ProgramCommand
import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.domain.ProgramDetail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(ProgramController::class)
class ProgramControllerTest : DescribeSpec() {
    override val extensions = listOf(SpringExtension())

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var programCommand: ProgramCommand

    private val program =
        Program(
            id = 1,
            name = "test",
            executedFileId = 2,
            status = Program.Status.COMPLETED,
            drops = 3,
            size = 4,
            duration = 5.0,
            recordedAt = LocalDateTime.MIN,
            channel = "",
            title = "",
            channelName = "",
        )

    private val programDetail =
        ProgramDetail(
            id = 1,
            name = "test",
            executedFileId = 2,
            status = Program.Status.COMPLETED,
            drops = 3,
            size = 4,
            duration = 5.0,
            recordedAt = LocalDateTime.MIN,
            channel = "",
            title = "",
            channelName = "",
            createdFiles = listOf(),
        )

    init {
        describe("index") {
            it("success") {
                every { programCommand.selectByName("", 100, 0) } returns listOf()
                mockMvc
                    .perform(get("/api/v1/programs"))
                    .andDo(print())
                    .andExpect(status().isOk)
            }

            it("success with parameters") {
                every { programCommand.selectByName("foo", 10, 5) } returns listOf()
                mockMvc
                    .perform(get("/api/v1/programs").param("name", "foo").param("limit", "10").param("offset", "5"))
                    .andDo(print())
                    .andExpect(status().isOk)
            }
        }

        describe("detail") {
            it("success") {
                every { programCommand.find(1L) } returns program
                every { programCommand.findDetail(1L) } returns programDetail
                every { programCommand.videoFiles(program) } returns listOf()

                mockMvc
                    .perform(get("/api/v1/programs/1"))
                    .andDo(print())
                    .andExpect(status().isOk)
            }

            it("not found when find returns null") {
                every { programCommand.find(1L) } returns null

                mockMvc
                    .perform(get("/api/v1/programs/1"))
                    .andDo(print())
                    .andExpect(status().isNotFound)
            }

            it("not found when findDetail returns null") {
                every { programCommand.find(1L) } returns program
                every { programCommand.findDetail(1L) } returns null

                mockMvc
                    .perform(get("/api/v1/programs/1"))
                    .andDo(print())
                    .andExpect(status().isNotFound)
            }
        }

        describe("delete") {
            it("not implemented") {
                mockMvc
                    .perform(delete("/api/v1/programs/1"))
                    .andDo(print())
                    .andExpect(status().isNotImplemented)
            }
        }
    }
}
