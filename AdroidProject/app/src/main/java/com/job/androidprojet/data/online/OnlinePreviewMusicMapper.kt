package com.job.androidprojet.data.online

import com.job.androidprojet.model.Music

private const val ONLINE_PREVIEW_DURATION_MILLIS = 30_000L
private const val ONLINE_PREVIEW_ID_BASE = -1_000_000_000_000L

fun OnlineMusicResult.toPreviewMusic(): Music? {
    val playableUrl = previewUrl?.takeIf { url ->
        url.startsWith(prefix = "https://", ignoreCase = true)
    } ?: return null

    return Music(
        id = previewMusicId(),
        title = title,
        artist = artist,
        album = album ?: "${source.label} 30s preview",
        albumImage = artworkUrl ?: "online_preview",
        fileName = playableUrl,
        durationMillis = ONLINE_PREVIEW_DURATION_MILLIS,
        previewUrl = playableUrl,
        artworkUrl = artworkUrl,
        sourceLabel = "${source.label} preview",
        onlinePreviewId = id,
        isOnlinePreview = true,
    )
}

private fun OnlineMusicResult.previewMusicId(): Long {
    val stableHash = "${source.name}:$id:$title:$artist".hashCode().toLong() and 0x00000000FFFFFFFFL
    return ONLINE_PREVIEW_ID_BASE - stableHash
}
