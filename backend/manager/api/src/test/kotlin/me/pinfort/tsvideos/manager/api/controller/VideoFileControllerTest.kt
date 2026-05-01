package me.pinfort.tsvideos.manager.api.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import me.pinfort.tsvideos.core.command.CreatedFileCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.InputStream

@WebMvcTest(VideoFileController::class)
class VideoFileControllerTest : DescribeSpec() {
    override val extensions = listOf(SpringExtension())

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var createdFileCommand: CreatedFileCommand

    init {
        describe("stream") {
            it("success") {
                every { createdFileCommand.streamCreatedFile(1L) } returns InputStream.nullInputStream()

                mockMvc
                    .perform(get("/api/v1/video/1/stream"))
                    .andDo(print())
                    .andExpect(status().isOk)
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "video/mp4"))
            }

            it("not found") {
                every { createdFileCommand.streamCreatedFile(1L) } returns null

                mockMvc
                    .perform(get("/api/v1/video/1/stream"))
                    .andDo(print())
                    .andExpect(status().isNotFound)
            }
        }
    }
}
