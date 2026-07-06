package com.job.androidprojet.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.job.androidprojet.data.SampleMusicCatalog
import com.job.androidprojet.model.Music
import com.job.androidprojet.ui.theme.AndroidProjetTheme

@Preview(
    name = "Home Screen",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
)
@Composable
private fun HomeScreenPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        HomeScreen(musicList = SampleMusicCatalog.songs)
    }
}

@Preview(
    name = "Home Screen Compact",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 320,
    heightDp = 640,
)
@Composable
private fun HomeScreenCompactPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        HomeScreen(musicList = SampleMusicCatalog.songs)
    }
}

@Preview(
    name = "Home Screen Search",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 360,
    heightDp = 720,
)
@Composable
private fun HomeScreenSearchPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        HomeScreen(
            musicList = SampleMusicCatalog.songs,
            initialDestinationName = "Search",
            initialSearchQuery = "drive",
        )
    }
}

@Preview(
    name = "Home Screen Search Empty",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 360,
    heightDp = 720,
)
@Composable
private fun HomeScreenSearchEmptyPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        HomeScreen(
            musicList = SampleMusicCatalog.songs,
            initialDestinationName = "Search",
            initialSearchQuery = "zzzz",
        )
    }
}

@Preview(
    name = "Home Screen Library",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 360,
    heightDp = 720,
)
@Composable
private fun HomeScreenLibraryPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        HomeScreen(
            musicList = SampleMusicCatalog.songs,
            initialDestinationName = "Library",
            initialFilterName = "Favorites",
        )
    }
}

@Preview(
    name = "Home Screen Player",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 360,
    heightDp = 780,
)
@Composable
private fun HomeScreenPlayerPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        HomeScreen(
            musicList = SampleMusicCatalog.songs,
            initialDestinationName = "Player",
        )
    }
}

@Preview(
    name = "Home Screen Player Compact",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 320,
    heightDp = 720,
)
@Composable
private fun HomeScreenPlayerCompactPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        HomeScreen(
            musicList = SampleMusicCatalog.songs,
            initialDestinationName = "Player",
        )
    }
}

@Preview(
    name = "Home Screen Library No Favorites",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 360,
    heightDp = 720,
)
@Composable
private fun HomeScreenLibraryNoFavoritesPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        HomeScreen(
            musicList = SampleMusicCatalog.songs.map { music ->
                music.copy(isFavorite = false)
            },
            initialDestinationName = "Library",
            initialFilterName = "Favorites",
        )
    }
}

@Preview(
    name = "Player Detail",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 360,
    heightDp = 760,
)
@Composable
private fun PlayerDetailPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        Box(
            modifier = Modifier
                .background(HomeBackground)
                .padding(16.dp),
        ) {
            PlayerDetailScreen(
                music = SampleMusicCatalog.songs.first(),
                isPlaying = true,
                positionMillis = 72_000L,
                progress = 0.36f,
                queuePreview = SampleMusicCatalog.songs.drop(1).take(3),
                onProgressChange = {},
                onTogglePlay = {},
                onPrevious = {},
                onNext = {},
                onToggleFavorite = {},
                onQueueTrackClick = {},
            )
        }
    }
}

@Preview(
    name = "Player Detail Compact Long Text",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 320,
    heightDp = 700,
)
@Composable
private fun PlayerDetailCompactLongTextPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        Box(
            modifier = Modifier
                .background(HomeBackground)
                .padding(16.dp),
        ) {
            PlayerDetailScreen(
                music = Music(
                    id = 101L,
                    title = "Late Night Drive Through the Longest Neon Avenue",
                    artist = "Sample Artist With A Very Long Name",
                    album = "Extended Local Sample Collection",
                    albumImage = "album_long_text",
                    fileName = "sample_long_text.mp3",
                    durationMillis = 367_000L,
                    isFavorite = true,
                ),
                isPlaying = false,
                positionMillis = 138_000L,
                progress = 0.38f,
                queuePreview = SampleMusicCatalog.songs.take(2),
                onProgressChange = {},
                onTogglePlay = {},
                onPrevious = {},
                onNext = {},
                onToggleFavorite = {},
                onQueueTrackClick = {},
            )
        }
    }
}

@Preview(
    name = "Quick Access",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 320,
)
@Composable
private fun QuickAccessPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        Box(
            modifier = Modifier
                .background(HomeBackground)
                .padding(16.dp),
        ) {
            QuickAccessGrid(
                music = SampleMusicCatalog.songs.take(4),
                onMusicClick = {},
            )
        }
    }
}

@Preview(
    name = "Music Row Long Text",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 320,
)
@Composable
private fun MusicListRowLongTextPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        MusicListRow(
            music = Music(
                id = 99L,
                title = "Late Night Drive Through the Longest Neon Avenue",
                artist = "Sample Artist With A Very Long Name",
                album = "Extended Local Sample Collection",
                albumImage = "album_long_text",
                fileName = "sample_long_text.mp3",
                durationMillis = 367_000L,
                isFavorite = true,
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(
    name = "Mini Player",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 360,
)
@Composable
private fun MiniPlayerPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        MiniPlayerBar(
            music = SampleMusicCatalog.songs.first(),
            isPlaying = true,
            onTogglePlay = {},
            onPrevious = {},
            onNext = {},
            onOpenPlayer = {},
        )
    }
}

@Preview(
    name = "Mini Player Compact Long Text",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
    widthDp = 320,
)
@Composable
private fun MiniPlayerCompactLongTextPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        MiniPlayerBar(
            music = Music(
                id = 100L,
                title = "Late Night Drive Through the Longest Neon Avenue",
                artist = "Sample Artist With A Very Long Name",
                album = "Extended Local Sample Collection",
                albumImage = "album_long_text",
                fileName = "sample_long_text.mp3",
                durationMillis = 367_000L,
            ),
            isPlaying = false,
            onTogglePlay = {},
            onPrevious = {},
            onNext = {},
            onOpenPlayer = {},
        )
    }
}

@Preview(
    name = "Home Screen Empty",
    showBackground = true,
    backgroundColor = 0xFF0E1411,
)
@Composable
private fun HomeScreenEmptyPreview() {
    AndroidProjetTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        HomeScreen(musicList = emptyList())
    }
}
