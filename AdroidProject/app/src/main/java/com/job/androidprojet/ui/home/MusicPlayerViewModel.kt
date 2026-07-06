package com.job.androidprojet.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.job.androidprojet.data.SampleMusicCatalog
import com.job.androidprojet.model.Music
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MusicContentFilter(val label: String) {
    Music("Music"),
    Favorites("Favorites"),
    Albums("Albums");

    companion object {
        fun fromName(name: String): MusicContentFilter {
            return entries.firstOrNull { filter ->
                filter.name.equals(name, ignoreCase = true) ||
                    filter.label.equals(name, ignoreCase = true)
            } ?: Music
        }
    }
}

data class MusicPlayerUiState(
    val musicList: List<Music> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: MusicContentFilter = MusicContentFilter.Music,
    val filteredMusic: List<Music> = emptyList(),
    val recentMusic: List<Music> = emptyList(),
    val currentMusic: Music? = null,
    val isPlaying: Boolean = false,
    val playbackProgress: Float = 0f,
    val currentPositionMillis: Long = 0L,
    val queuePreview: List<Music> = emptyList(),
)

class MusicPlayerViewModel(
    initialMusicList: List<Music> = SampleMusicCatalog.songs,
    initialSearchQuery: String = "",
    initialFilterName: String = MusicContentFilter.Music.name,
) : ViewModel() {
    private var musicList: List<Music> = initialMusicList
    private var searchQuery: String = initialSearchQuery
    private var selectedFilter: MusicContentFilter = MusicContentFilter.fromName(initialFilterName)
    private var selectedMusicId: Long? = initialMusicList.firstOrNull()?.id
    private var isPlaying: Boolean = false
    private var playbackProgress: Float = 0f
    private var recentMusicIds: List<Long> = emptyList()

    private val _uiState = MutableStateFlow(buildUiState())
    val uiState: StateFlow<MusicPlayerUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        searchQuery = query
        emitState()
    }

    fun selectFilter(filter: MusicContentFilter) {
        selectedFilter = filter
        emitState()
    }

    fun selectMusic(music: Music) {
        val selectedMusic = musicList.firstOrNull { track -> track.id == music.id } ?: return
        selectedMusicId = selectedMusic.id
        isPlaying = true
        playbackProgress = 0f
        recordRecentSelection(selectedMusic.id)
        emitState()
    }

    fun togglePlay() {
        if (musicList.isEmpty()) return
        isPlaying = !isPlaying
        emitState()
    }

    fun previousTrack() {
        selectRelativeTrack(offset = -1)
    }

    fun nextTrack() {
        selectRelativeTrack(offset = 1)
    }

    fun updateProgress(progress: Float) {
        playbackProgress = progress.coerceIn(0f, 1f)
        emitState()
    }

    fun syncPlaybackState(
        musicId: Long?,
        isPlaying: Boolean,
        progress: Float,
    ) {
        if (musicId != null && musicList.any { music -> music.id == musicId }) {
            selectedMusicId = musicId
        }
        this.isPlaying = isPlaying
        playbackProgress = progress.coerceIn(0f, 1f)
        emitState()
    }

    fun toggleFavorite() {
        val currentMusic = currentMusic() ?: return
        musicList = musicList.map { music ->
            if (music.id == currentMusic.id) {
                music.copy(isFavorite = !music.isFavorite)
            } else {
                music
            }
        }
        emitState()
    }

    private fun selectRelativeTrack(offset: Int) {
        if (musicList.isEmpty()) return
        val currentIndex = musicList.indexOfFirst { music -> music.id == currentMusic()?.id }
            .takeIf { index -> index >= 0 }
            ?: 0
        val nextIndex = floorMod(currentIndex + offset, musicList.size)
        val nextMusic = musicList[nextIndex]
        selectedMusicId = nextMusic.id
        isPlaying = true
        playbackProgress = 0f
        recordRecentSelection(nextMusic.id)
        emitState()
    }

    private fun emitState() {
        selectedMusicId = musicList.firstOrNull { music -> music.id == selectedMusicId }?.id
            ?: musicList.firstOrNull()?.id
        recentMusicIds = recentMusicIds.filter { id ->
            musicList.any { music -> music.id == id }
        }
        playbackProgress = playbackProgress.coerceIn(0f, 1f)
        _uiState.value = buildUiState()
    }

    private fun buildUiState(): MusicPlayerUiState {
        val currentMusic = currentMusic()
        val durationMillis = currentMusic?.durationMillis ?: 0L
        val currentPositionMillis = (durationMillis * playbackProgress).toLong()
            .coerceIn(0L, durationMillis)

        return MusicPlayerUiState(
            musicList = musicList,
            searchQuery = searchQuery,
            selectedFilter = selectedFilter,
            filteredMusic = filterMusic(
                musicList = musicList,
                query = searchQuery,
                filter = selectedFilter,
            ),
            recentMusic = recentMusicIds.mapNotNull { recentId ->
                musicList.firstOrNull { music -> music.id == recentId }
            },
            currentMusic = currentMusic,
            isPlaying = isPlaying,
            playbackProgress = playbackProgress,
            currentPositionMillis = currentPositionMillis,
            queuePreview = nextTracks(
                musicList = musicList,
                currentMusic = currentMusic,
                count = 3,
            ),
        )
    }

    private fun currentMusic(): Music? {
        return musicList.firstOrNull { music -> music.id == selectedMusicId }
            ?: musicList.firstOrNull()
    }

    private fun recordRecentSelection(musicId: Long) {
        recentMusicIds = (listOf(musicId) + recentMusicIds.filterNot { id -> id == musicId })
            .take(MAX_RECENT_MUSIC_COUNT)
    }

    companion object {
        private const val MAX_RECENT_MUSIC_COUNT = 8

        fun factory(
            musicList: List<Music>,
            initialSearchQuery: String = "",
            initialFilterName: String = MusicContentFilter.Music.name,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MusicPlayerViewModel::class.java)) {
                        return MusicPlayerViewModel(
                            initialMusicList = musicList,
                            initialSearchQuery = initialSearchQuery,
                            initialFilterName = initialFilterName,
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}

private fun filterMusic(
    musicList: List<Music>,
    query: String,
    filter: MusicContentFilter,
): List<Music> {
    val normalizedQuery = query.trim()
    val queryMatches = if (normalizedQuery.isBlank()) {
        musicList
    } else {
        musicList.filter { music ->
            music.title.contains(normalizedQuery, ignoreCase = true) ||
                music.artist.contains(normalizedQuery, ignoreCase = true) ||
                music.album.contains(normalizedQuery, ignoreCase = true)
        }
    }

    return when (filter) {
        MusicContentFilter.Music -> queryMatches
        MusicContentFilter.Favorites -> queryMatches.filter { music -> music.isFavorite }
        MusicContentFilter.Albums -> queryMatches.distinctBy { music -> music.album }
    }
}

private fun nextTracks(
    musicList: List<Music>,
    currentMusic: Music?,
    count: Int,
): List<Music> {
    if (musicList.size <= 1 || currentMusic == null || count <= 0) return emptyList()

    val currentIndex = musicList.indexOfFirst { music -> music.id == currentMusic.id }
        .takeIf { index -> index >= 0 }
        ?: 0
    val previewCount = minOf(count, musicList.size - 1)

    return (1..previewCount).map { offset ->
        musicList[(currentIndex + offset) % musicList.size]
    }
}

private fun floorMod(value: Int, divisor: Int): Int {
    return ((value % divisor) + divisor) % divisor
}
