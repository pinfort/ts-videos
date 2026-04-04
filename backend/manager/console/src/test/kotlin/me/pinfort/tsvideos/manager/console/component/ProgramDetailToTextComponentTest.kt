package me.pinfort.tsvideos.manager.console.component

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import me.pinfort.tsvideos.core.domain.CreatedFile
import me.pinfort.tsvideos.core.domain.Program
import me.pinfort.tsvideos.core.domain.ProgramDetail
import java.time.LocalDateTime

class ProgramDetailToTextComponentTest : ExpectSpec({
    val programDetailToTextComponent = ProgramDetailToTextComponent()

    val programDetail = ProgramDetail(
        id = 1,
        name = "name",
        executedFileId = 2,
        status = Program.Status.COMPLETED,
        drops = 2,
        size = 3,
        recordedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0),
        channel = "channel",
        title = "title",
        channelName = "channelName",
        duration = 60.0,
        createdFiles = listOf(
            CreatedFile(
                id = 1,
                mime = "mime",
                file = "file",
                splittedFileId = 2,
                size = 3,
                encoding = "encoding",
                status = CreatedFile.Status.FILE_MOVED
            )
        )
    )

    context("convertConsole") {
        expect("success") {
            val actual = programDetailToTextComponent.convertConsole(programDetail)
            val expected = """
                番組ID: 1
                番組名: title
                放送局: channelName
                放送日時: 2023/01/01 00:00:00
                放送時間: 1m
                ファイル: 1件
                id	mime	name
                1	mime	file

            """.trimIndent()
            actual shouldBe expected
        }
    }
})
