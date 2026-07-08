package com.job.androidprojet.ui.home

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.job.androidprojet.data.online.OnlineMusicResult
import com.job.androidprojet.data.online.OnlineMusicSearchState
import com.job.androidprojet.model.Music

internal fun LazyListScope.homeContent(
    music: List<Music>,
    recentMusic: List<Music>,
    homePreviewState: OnlineMusicSearchState,
    previewMusicId: String?,
    previewIsPlaying: Boolean,
    onPreviewToggle: ((OnlineMusicResult, List<OnlineMusicResult>) -> Unit)?,
    onHomePreviewRetry: (() -> Unit)?,
    onMusicClick: (Music) -> Unit,
) {
    if (music.isEmpty()) {
        item {
            EmptyMusicList(message = "No local tracks match this view")
        }
        return
    }

    item {
        SectionTitle(title = "Jump back in")
    }

    homePreviewContent(
        homePreviewState = homePreviewState,
        previewMusicId = previewMusicId,
        previewIsPlaying = previewIsPlaying,
        onPreviewToggle = onPreviewToggle,
        onRetry = onHomePreviewRetry,
    )

    item {
        QuickAccessGrid(
            music = music.take(6),
            onMusicClick = onMusicClick,
        )
    }

    val visibleMusicIds = music.map { track -> track.id }.toSet()
    val visibleRecentMusic = recentMusic.filter { track -> track.id in visibleMusicIds }
    item {
        HorizontalMusicSection(
            title = "Recent selections",
            music = visibleRecentMusic,
            onMusicClick = onMusicClick,
            emptyMessage = "Select a track to build your recent history",
        )
    }

    item {
        HorizontalMusicSection(
            title = "Favorites",
            music = music.filter { track -> track.isFavorite },
            onMusicClick = onMusicClick,
            emptyMessage = "Tap Save on the player screen to keep tracks here",
        )
    }

    item {
        HorizontalMusicSection(
            title = "Made for focus",
            music = music.filter { track ->
                track.album.contains("Focus", ignoreCase = true) ||
                    track.artist.contains("Lo-Fi", ignoreCase = true) ||
                    track.artist.contains("Calm", ignoreCase = true)
            }.ifEmpty { music.drop(2).take(5) },
            onMusicClick = onMusicClick,
        )
    }

    item {
        SectionTitle(title = "All local tracks")
    }

    items(
        items = music,
        key = { track -> "home-${track.id}" },
    ) { track ->
        MusicListRow(
            music = track,
            onClick = { onMusicClick(track) },
        )
    }
}

internal fun LazyListScope.searchContent(
    music: List<Music>,
    query: String,
    onlineSearchState: OnlineMusicSearchState,
    previewMusicId: String?,
    previewIsPlaying: Boolean,
    onPreviewToggle: ((OnlineMusicResult, List<OnlineMusicResult>) -> Unit)?,
    onOnlineSearchRetry: (() -> Unit)?,
    onMusicClick: (Music) -> Unit,
) {
    localSearchResultsContent(
        music = music,
        query = query,
        onMusicClick = onMusicClick,
    )

    onlineSearchContent(
        query = query,
        onlineSearchState = onlineSearchState,
        previewMusicId = previewMusicId,
        previewIsPlaying = previewIsPlaying,
        onPreviewToggle = onPreviewToggle,
        onRetry = onOnlineSearchRetry,
    )
}

private fun LazyListScope.localSearchResultsContent(
    music: List<Music>,
    query: String,
    onMusicClick: (Music) -> Unit,
) {
    item {
        SectionTitle(
            title = if (query.isBlank()) "Browse local music" else "Search results",
            subtitle = "Matches title, artist, and album",
        )
    }

    if (music.isEmpty()) {
        item {
            EmptyMusicList(message = "No local tracks found")
        }
        return
    }

    items(
        items = music,
        key = { track -> "search-${track.id}" },
    ) { track ->
        MusicListRow(
            music = track,
            onClick = { onMusicClick(track) },
        )
    }
}

internal fun LazyListScope.libraryContent(
    music: List<Music>,
    allMusic: List<Music>,
    recentMusic: List<Music>,
    onMusicClick: (Music) -> Unit,
) {
    item {
        LibraryStatsRow(allMusic = allMusic)
    }

    val favorites = music.filter { track -> track.isFavorite }
    item {
        HorizontalMusicSection(
            title = "Favorites",
            music = favorites,
            onMusicClick = onMusicClick,
            emptyMessage = if (allMusic.any { track -> track.isFavorite }) {
                "No favorites in this filter"
            } else {
                "Tap the favorite button on a track to save it here"
            },
        )
    }

    val visibleMusicIds = music.map { track -> track.id }.toSet()
    val visibleRecentMusic = recentMusic.filter { track -> track.id in visibleMusicIds }
    item {
        HorizontalMusicSection(
            title = "Recent selections",
            music = visibleRecentMusic,
            onMusicClick = onMusicClick,
            emptyMessage = if (recentMusic.isEmpty()) {
                "Select a track to build your recent history"
            } else {
                "No recent selections in this filter"
            },
        )
    }

    item {
        SectionTitle(title = "Albums")
    }

    val albumRepresentatives = music.distinctBy { track -> track.album }
    if (albumRepresentatives.isEmpty()) {
        item {
            EmptyMusicList(message = "No albums match this view")
        }
        return
    }

    items(
        items = albumRepresentatives,
        key = { track -> "album-${track.album}-${track.id}" },
    ) { track ->
        MusicListRow(
            music = track,
            onClick = { onMusicClick(track) },
        )
    }
}
