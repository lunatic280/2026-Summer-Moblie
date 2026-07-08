package com.job.androidprojet.widget

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.job.androidprojet.data.SampleMusicCatalog
import com.job.androidprojet.playback.MusicPlaybackService
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MusicPlayerWidgetProviderTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @After
    fun tearDown() {
        MusicWidgetStateStore.clear(context)
    }

    @Test
    fun stalePlayingPauseCommandMarksWidgetPaused() {
        val music = SampleMusicCatalog.songs.first()
        MusicWidgetStateStore.saveCurrentTrack(
            context = context,
            musicId = music.id,
            title = music.title,
            artist = music.artist,
            album = music.album,
            isPlaying = true,
        )

        MusicPlayerWidgetProvider().onReceive(
            context,
            Intent(context, MusicPlayerWidgetProvider::class.java)
                .setAction(MusicPlaybackService.ACTION_WIDGET_PLAY_PAUSE)
                .putExtra(MusicPlaybackService.EXTRA_WIDGET_SHOULD_PLAY, false),
        )

        assertFalse(MusicWidgetStateStore.current(context).isPlaying)
    }

    @Test
    fun previewPauseCommandMarksWidgetPaused() {
        MusicWidgetStateStore.saveCurrentTrack(
            context = context,
            musicId = 9001L,
            title = "Preview",
            artist = "API preview",
            album = "Online preview",
            previewUrl = "https://example.com/preview.mp3",
            isPlaying = true,
        )

        MusicPlayerWidgetProvider().onReceive(
            context,
            Intent(context, MusicPlayerWidgetProvider::class.java)
                .setAction(MusicPlaybackService.ACTION_WIDGET_PLAY_PAUSE)
                .putExtra(MusicPlaybackService.EXTRA_WIDGET_SHOULD_PLAY, false),
        )

        assertFalse(MusicWidgetStateStore.current(context).isPlaying)
    }
}
