package com.job.androidprojet.playback

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.job.androidprojet.model.Music

fun Music.toLocalMediaItem(context: Context): MediaItem? {
    val appContext = context.applicationContext
    val rawResourceId = appContext.rawResourceIdFor(this)
    if (rawResourceId == 0) return null

    val uri = Uri.Builder()
        .scheme("android.resource")
        .authority(appContext.packageName)
        .appendPath(rawResourceId.toString())
        .build()

    return MediaItem.Builder()
        .setUri(uri)
        .setMediaId(id.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .build(),
        )
        .build()
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
