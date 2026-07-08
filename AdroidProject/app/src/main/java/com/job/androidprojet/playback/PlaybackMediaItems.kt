package com.job.androidprojet.playback

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.job.androidprojet.model.Music

fun Music.toPlaybackMediaItem(context: Context): MediaItem? {
    val previewUri = previewUrl?.let(Uri::parse)
    if (previewUri != null) {
        return buildMediaItem(uri = previewUri)
    }

    return toLocalMediaItem(context)
}

fun Music.toLocalMediaItem(context: Context): MediaItem? {
    val appContext = context.applicationContext
    val rawResourceId = appContext.rawResourceIdFor(this)
    if (rawResourceId == 0) return null

    val uri = Uri.Builder()
        .scheme("android.resource")
        .authority(appContext.packageName)
        .appendPath(rawResourceId.toString())
        .build()

    return buildMediaItem(uri = uri)
}

private fun Music.buildMediaItem(uri: Uri): MediaItem {
    val artworkUri = artworkUrl
        ?.takeIf { url -> url.isNotBlank() }
        ?.let(Uri::parse)

    return MediaItem.Builder()
        .setUri(uri)
        .setMediaId(id.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setDisplayTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setAlbumArtist(artist)
                .setSubtitle(metadataSubtitle())
                .setDescription(metadataDescription())
                .setDurationMs(durationMillis)
                .setIsPlayable(true)
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .setArtworkUri(artworkUri)
                .build(),
        )
        .build()
}

private fun Music.metadataSubtitle(): String {
    return listOf(artist, album)
        .filter { value -> value.isNotBlank() }
        .distinct()
        .joinToString(" - ")
}

private fun Music.metadataDescription(): String {
    val sourcePrefix = if (isOnlinePreview) "$sourceLabel: " else ""
    return "$sourcePrefix$title by $artist from $album"
}

private fun Context.rawResourceIdFor(music: Music): Int {
    val resourceName = music.fileName
        .substringBeforeLast(".")
        .lowercase()
        .replace(Regex("[^a-z0-9_]"), "_")

    return resources.getIdentifier(
        resourceName,
        "raw",
        packageName,
    )
}
