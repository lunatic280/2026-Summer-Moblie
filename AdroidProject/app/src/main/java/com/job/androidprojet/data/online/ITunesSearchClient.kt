package com.job.androidprojet.data.online

import java.net.URLEncoder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ITunesSearchClient(
    private val httpClient: SimpleHttpClient = SimpleHttpClient(),
) : OnlineMusicSearchProvider {
    override val source: OnlineMusicSource = OnlineMusicSource.ITunes

    override suspend fun search(
        query: String,
    ): List<OnlineMusicResult> = search(
        query = query,
        countries = DEFAULT_COUNTRIES,
    )

    suspend fun search(
        query: String,
        countries: List<String> = DEFAULT_COUNTRIES,
    ): List<OnlineMusicResult> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())
        val countryResults = countries.map { country ->
            searchCountry(encodedQuery = encodedQuery, country = country)
        }
        val successfulResults = countryResults
            .mapNotNull { result -> result.getOrNull() }
            .flatten()

        if (successfulResults.isNotEmpty() || countryResults.any { result -> result.isSuccess }) {
            successfulResults
        } else {
            throw countryResults.firstNotNullOfOrNull { result -> result.exceptionOrNull() }
                ?: IllegalStateException("No iTunes countries are configured.")
        }
    }

    private fun searchCountry(
        encodedQuery: String,
        country: String,
    ): Result<List<OnlineMusicResult>> {
        return try {
            val url = "https://itunes.apple.com/search?term=$encodedQuery" +
                "&media=music&entity=song&country=$country&limit=8"
            val response = json.decodeFromString<ITunesSearchResponse>(httpClient.get(url))
            Result.success(
                response.results.mapNotNull { item ->
                    val title = item.trackName ?: item.collectionName ?: return@mapNotNull null
                    val artist = item.artistName ?: return@mapNotNull null
                    OnlineMusicResult(
                        id = "itunes-$country-${item.trackId ?: "$title-$artist"}",
                        title = title,
                        artist = artist,
                        album = item.collectionName,
                        source = OnlineMusicSource.ITunes,
                        previewUrl = item.previewUrl,
                        artworkUrl = item.artworkUrl100,
                        releaseDate = item.releaseDate?.take(10),
                    )
                },
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    private companion object {
        val DEFAULT_COUNTRIES = listOf("KR", "IN", "US")
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
}

@Serializable
private data class ITunesSearchResponse(
    val results: List<ITunesTrackDto> = emptyList(),
)

@Serializable
private data class ITunesTrackDto(
    val trackId: Long? = null,
    val trackName: String? = null,
    val artistName: String? = null,
    val collectionName: String? = null,
    val previewUrl: String? = null,
    val artworkUrl100: String? = null,
    val releaseDate: String? = null,
)
