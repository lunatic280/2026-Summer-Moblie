package com.job.androidprojet.ui.home

import com.job.androidprojet.data.online.OnlineMusicResult
import com.job.androidprojet.data.online.OnlineMusicSource
import com.job.androidprojet.model.Music
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MusicPlayerViewModelTest {
    @Test
    fun initialState_hasNoCurrentTrackBeforeSelection() {
        val viewModel = MusicPlayerViewModel(initialMusicList = testMusic)

        assertEquals(null, viewModel.uiState.value.currentMusic)
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun updateSearchQuery_filtersByTitleArtistAndAlbum() {
        val viewModel = MusicPlayerViewModel(initialMusicList = testMusic)

        viewModel.updateSearchQuery("focus")

        assertEquals(
            listOf("Study Beats", "Deep Focus"),
            viewModel.uiState.value.filteredMusic.map { music -> music.title },
        )
    }

    @Test
    fun selectMusic_setsCurrentTrackAndStartsPlayback() {
        val viewModel = MusicPlayerViewModel(initialMusicList = testMusic)

        viewModel.selectMusic(testMusic[1])

        assertEquals("Study Beats", viewModel.uiState.value.currentMusic?.title)
        assertTrue(viewModel.uiState.value.isPlaying)
        assertEquals(0f, viewModel.uiState.value.playbackProgress)
        assertEquals(
            listOf("Study Beats"),
            viewModel.uiState.value.recentMusic.map { music -> music.title },
        )
    }

    @Test
    fun selectOnlinePreview_setsCurrentTrackAndUsesThirtySecondDuration() {
        val viewModel = MusicPlayerViewModel(initialMusicList = testMusic)

        viewModel.selectOnlinePreview(
            result = testPreviewResults.first(),
            recommendations = testPreviewResults,
        )

        assertEquals("Preview One", viewModel.uiState.value.currentMusic?.title)
        assertTrue(viewModel.uiState.value.currentMusic?.isOnlinePreview == true)
        assertEquals(30_000L, viewModel.uiState.value.currentMusic?.durationMillis)
        assertTrue(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun nextTrack_whenOnlinePreviewSelected_usesPreviewQueue() {
        val viewModel = MusicPlayerViewModel(initialMusicList = testMusic)

        viewModel.selectOnlinePreview(
            result = testPreviewResults.first(),
            recommendations = testPreviewResults,
        )
        viewModel.nextTrack()

        assertEquals("Preview Two", viewModel.uiState.value.currentMusic?.title)
        assertTrue(viewModel.uiState.value.currentMusic?.isOnlinePreview == true)
    }

    @Test
    fun toggleFavorite_updatesFavoritesFilter() {
        val viewModel = MusicPlayerViewModel(
            initialMusicList = testMusic,
            initialFilterName = MusicContentFilter.Favorites.name,
        )

        viewModel.selectMusic(testMusic[1])
        viewModel.toggleFavorite()

        assertEquals(
            listOf("Night Drive", "Study Beats"),
            viewModel.uiState.value.filteredMusic.map { music -> music.title },
        )
    }

    @Test
    fun nextTrack_wrapsAroundCatalog() {
        val viewModel = MusicPlayerViewModel(initialMusicList = testMusic)

        viewModel.selectMusic(testMusic.last())
        viewModel.nextTrack()

        assertEquals("Night Drive", viewModel.uiState.value.currentMusic?.title)
        assertTrue(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun selectMusic_ordersRecentHistoryMostRecentFirstWithoutDuplicates() {
        val viewModel = MusicPlayerViewModel(initialMusicList = testMusic)

        viewModel.selectMusic(testMusic[0])
        viewModel.selectMusic(testMusic[1])
        viewModel.selectMusic(testMusic[0])

        assertEquals(
            listOf("Night Drive", "Study Beats"),
            viewModel.uiState.value.recentMusic.map { music -> music.title },
        )
    }

    @Test
    fun selectMusic_capsRecentHistory() {
        val catalog = (1L..10L).map { id ->
            Music(
                id = id,
                title = "Track $id",
                artist = "Sample Artist",
                album = "Sample Album",
                albumImage = "album_$id",
                fileName = "sample_$id.mp3",
                durationMillis = 180_000L,
            )
        }
        val viewModel = MusicPlayerViewModel(initialMusicList = catalog)

        catalog.forEach { music -> viewModel.selectMusic(music) }

        assertEquals(
            listOf(
                "Track 10",
                "Track 9",
                "Track 8",
                "Track 7",
                "Track 6",
                "Track 5",
                "Track 4",
                "Track 3",
            ),
            viewModel.uiState.value.recentMusic.map { music -> music.title },
        )
    }

    @Test
    fun previousAndNextTrack_updateRecentHistory() {
        val viewModel = MusicPlayerViewModel(initialMusicList = testMusic)

        viewModel.selectMusic(testMusic[1])
        viewModel.previousTrack()
        viewModel.nextTrack()

        assertEquals("Study Beats", viewModel.uiState.value.currentMusic?.title)
        assertEquals(
            listOf("Study Beats", "Night Drive"),
            viewModel.uiState.value.recentMusic.map { music -> music.title },
        )
    }

    @Test
    fun updateProgress_clampsToValidRange() {
        val viewModel = MusicPlayerViewModel(initialMusicList = testMusic)

        viewModel.selectMusic(testMusic.first())
        viewModel.updateProgress(1.4f)

        assertEquals(1f, viewModel.uiState.value.playbackProgress)
        assertEquals(
            testMusic.first().durationMillis,
            viewModel.uiState.value.currentPositionMillis,
        )
    }

    @Test
    fun togglePlay_doesNothingForEmptyCatalog() {
        val viewModel = MusicPlayerViewModel(initialMusicList = emptyList())

        viewModel.togglePlay()

        assertFalse(viewModel.uiState.value.isPlaying)
    }

    private companion object {
        val testMusic = listOf(
            Music(
                id = 1L,
                title = "Night Drive",
                artist = "Sample Artist",
                album = "City Lights",
                albumImage = "album_night_drive",
                fileName = "sample_night_drive.mp3",
                durationMillis = 200_000L,
                isFavorite = true,
            ),
            Music(
                id = 2L,
                title = "Study Beats",
                artist = "Lo-Fi Studio",
                album = "Focus Room",
                albumImage = "album_study_beats",
                fileName = "sample_study_beats.mp3",
                durationMillis = 184_000L,
            ),
            Music(
                id = 3L,
                title = "Deep Focus",
                artist = "Lo-Fi Studio",
                album = "Focus Room",
                albumImage = "album_deep_focus",
                fileName = "sample_deep_focus.mp3",
                durationMillis = 204_000L,
            ),
        )

        val testPreviewResults = listOf(
            OnlineMusicResult(
                id = "preview-1",
                title = "Preview One",
                artist = "API Artist",
                album = "API Album",
                source = OnlineMusicSource.ITunes,
                previewUrl = "https://example.com/preview-one.m4a",
            ),
            OnlineMusicResult(
                id = "preview-2",
                title = "Preview Two",
                artist = "API Artist",
                album = "API Album",
                source = OnlineMusicSource.ITunes,
                previewUrl = "https://example.com/preview-two.m4a",
            ),
        )
    }
}
