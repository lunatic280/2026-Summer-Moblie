package com.job.androidprojet.data.online

import java.net.HttpURLConnection
import java.net.URL

class SimpleHttpClient {
    fun get(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MILLIS
            readTimeout = TIMEOUT_MILLIS
            setRequestProperty("Accept", "application/json")
            setRequestProperty(
                "User-Agent",
                "AndroidProjetMusicPlayer/1.0 (https://example.local/student-project)",
            )
        }

        return connection.use {
            val statusCode = responseCode
            val stream = if (statusCode in 200..299) inputStream else errorStream
            val body = stream?.bufferedReader()?.use { reader -> reader.readText() }.orEmpty()
            if (statusCode !in 200..299) {
                error("HTTP $statusCode from $url${body.takeIf { it.isNotBlank() }?.let { ": $it" }.orEmpty()}")
            }
            body
        }
    }

    private inline fun <T> HttpURLConnection.use(block: HttpURLConnection.() -> T): T {
        return try {
            block()
        } finally {
            disconnect()
        }
    }

    private companion object {
        const val TIMEOUT_MILLIS = 10_000
    }
}
