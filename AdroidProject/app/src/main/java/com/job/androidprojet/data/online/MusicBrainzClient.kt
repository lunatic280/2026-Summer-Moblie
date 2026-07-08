package com.job.androidprojet.data.online

import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MusicBrainzClient(
    private val httpClient: SimpleHttpClient = SimpleHttpClient(),
) : OnlineMusicSearchProvider {
    override val source: OnlineMusicSource = OnlineMusicSource.MusicBrainz

    override suspend fun search(query: String): List<OnlineMusicResult> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())
        val url = "https://musicbrainz.org/ws/2/recording?query=$encodedQuery&fmt=json&limit=8"
        val response = json.decodeFromString<MusicBrainzRecordingResponse>(httpClient.get(url))

        response.recordings.mapNotNull { recording ->
            val artist = recording.artistCredit
                .mapNotNull { credit -> credit.artist?.name ?: credit.name }
                .joinToString(separator = ", ")
                .ifBlank { return@mapNotNull null }
            val album = recording.releases.firstOrNull()?.title

            OnlineMusicResult(
                id = "musicbrainz-${recording.id}",
                title = recording.title,
                artist = artist,
                album = album,
                source = OnlineMusicSource.MusicBrainz,
                releaseDate = recording.firstReleaseDate,
            )
        }
    }

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
}

@Serializable
private data class MusicBrainzRecordingResponse(
    val recordings: List<MusicBrainzRecordingDto> = emptyList(),
)

@Serializable
private data class MusicBrainzRecordingDto(
    val id: String,
    val title: String,
    @SerialName("first-release-date")
    val firstReleaseDate: String? = null,
    @SerialName("artist-credit")
    val artistCredit: List<MusicBrainzArtistCreditDto> = emptyList(),
    val releases: List<MusicBrainzReleaseDto> = emptyList(),
)

@Serializable
private data class MusicBrainzArtistCreditDto(
    val name: String? = null,
    val artist: MusicBrainzArtistDto? = null,
)

@Serializable
private data class MusicBrainzArtistDto(
    val name: String? = null,
)

@Serializable
private data class MusicBrainzReleaseDto(
    val title: String? = null,
)
