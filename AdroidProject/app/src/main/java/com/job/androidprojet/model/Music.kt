package com.job.androidprojet.model

data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumImage: String,
    val fileName: String,
    val durationMillis: Long,
    val isFavorite: Boolean = false,
    val previewUrl: String? = null,
    val artworkUrl: String? = null,
    val sourceLabel: String = "Local",
    val onlinePreviewId: String? = null,
    val isOnlinePreview: Boolean = false,
)
