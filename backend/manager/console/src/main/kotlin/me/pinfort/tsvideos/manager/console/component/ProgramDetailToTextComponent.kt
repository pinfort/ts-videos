package me.pinfort.tsvideos.manager.console.component

import me.pinfort.tsvideos.core.domain.ProgramDetail
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Component
class ProgramDetailToTextComponent {
    private val datetimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

    fun convertConsole(programDetail: ProgramDetail): String {
        val videoFiles = programDetail.createdFiles
        val sb = StringBuilder()
        sb.appendLine("番組ID: ${programDetail.id}")
        sb.appendLine("番組名: ${programDetail.title}")
        sb.appendLine("放送局: ${programDetail.channelName}")
        sb.appendLine("放送日時: ${programDetail.recordedAt.format(datetimeFormat)}")
        sb.appendLine("放送時間: ${programDetail.duration.seconds.toString(DurationUnit.MINUTES)}")
        sb.appendLine("ファイル: ${videoFiles.size}件")
        sb.appendLine("id\tmime\tname")
        videoFiles.forEach {
            sb.appendLine("${it.id}\t${it.mime}\t${it.file}")
        }
        return sb.toString()
    }
}
