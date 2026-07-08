package com.job.androidprojet.widget

import android.content.Context

data class MusicWidgetState(
    val musicId: Long?,
    val title: String,
    val artist: String,
    val album: String,
    val previewUrl: String?,
    val artworkUrl: String?,
    val isPlaying: Boolean,
    val hasTrack: Boolean,
)

object MusicWidgetStateStore {
    private const val PREFS_NAME = "music_widget_state"
    private const val KEY_MUSIC_ID = "music_id"
    private const val KEY_TITLE = "title"
    private const val KEY_ARTIST = "artist"
    private const val KEY_ALBUM = "album"
    private const val KEY_PREVIEW_URL = "preview_url"
    private const val KEY_ARTWORK_URL = "artwork_url"
    private const val KEY_IS_PLAYING = "is_playing"

    private const val DEFAULT_TITLE = "No local track selected"
    private const val DEFAULT_ARTIST = "Open the app to choose a sample song"
    private const val DEFAULT_ALBUM = "Local player"

    fun current(context: Context): MusicWidgetState {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedMusicId = prefs.getLong(KEY_MUSIC_ID, NO_MUSIC_ID)
            .takeIf { musicId -> musicId != NO_MUSIC_ID }
        val storedTitle = prefs.getString(KEY_TITLE, null).cleanValue()
        val storedArtist = prefs.getString(KEY_ARTIST, null).cleanValue()
        val storedAlbum = prefs.getString(KEY_ALBUM, null).cleanValue()
        val storedPreviewUrl = prefs.getString(KEY_PREVIEW_URL, null).cleanValue()
        val storedArtworkUrl = prefs.getString(KEY_ARTWORK_URL, null).cleanValue()
        val hasTrack = storedTitle != null || storedArtist != null

        return MusicWidgetState(
            musicId = storedMusicId,
            title = storedTitle ?: DEFAULT_TITLE,
            artist = storedArtist ?: DEFAULT_ARTIST,
            album = storedAlbum ?: DEFAULT_ALBUM,
            previewUrl = storedPreviewUrl,
            artworkUrl = storedArtworkUrl,
            isPlaying = hasTrack && prefs.getBoolean(KEY_IS_PLAYING, false),
            hasTrack = hasTrack,
        )
    }

    fun saveCurrentTrack(
        context: Context,
        musicId: Long,
        title: String,
        artist: String,
        album: String,
        previewUrl: String? = null,
        artworkUrl: String? = null,
        isPlaying: Boolean,
    ) {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_MUSIC_ID, musicId)
            .putString(KEY_TITLE, title.trim())
            .putString(KEY_ARTIST, artist.trim())
            .putString(KEY_ALBUM, album.trim())
            .putString(KEY_PREVIEW_URL, previewUrl?.trim().orEmpty())
            .putString(KEY_ARTWORK_URL, artworkUrl?.trim().orEmpty())
            .putBoolean(KEY_IS_PLAYING, isPlaying)
            .commit()
    }

    fun setPlaying(context: Context, isPlaying: Boolean) {
        val state = current(context)
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_PLAYING, state.hasTrack && isPlaying)
            .commit()
    }

    fun clear(context: Context) {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    private fun String?.cleanValue(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

    private const val NO_MUSIC_ID = -1L
}
