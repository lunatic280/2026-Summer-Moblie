package com.job.androidprojet.data.online

data class OnlineMusicResult(
    val id: String,
    val title: String,
    val artist: String,
    val album: String?,
    val source: OnlineMusicSource,
    val previewUrl: String? = null,
    val artworkUrl: String? = null,
    val releaseDate: String? = null,
)

enum class OnlineMusicSource(val label: String) {
    ITunes("iTunes"),
    MusicBrainz("MusicBrainz"),
}

sealed interface OnlineMusicSearchState {
    data object Idle : OnlineMusicSearchState
    data object Loading : OnlineMusicSearchState
    data class Success(val results: List<OnlineMusicResult>) : OnlineMusicSearchState
    data class Error(val message: String) : OnlineMusicSearchState
}
