package me.pinfort.tsvideos.infrastructure.external.slack

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class SlackClient(
    @Value("\${SLACK_WEBHOOK_URL:}") private val webhookUrl: String,
) {
    fun notify(message: String) {
        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toJson(message)))
                .build()
        httpClient.send(request, HttpResponse.BodyHandlers.discarding())
    }

    // If the complexity increases, please replace it with Jackson
    private fun toJson(message: String): String {
        val escaped =
            message
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
        return "{\"text\":\"$escaped\"}"
    }

    private val httpClient = HttpClient.newHttpClient()
}
