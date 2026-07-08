package com.job.androidprojet.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.job.androidprojet.data.online.OnlineMusicResult
import com.job.androidprojet.data.online.OnlineMusicRepository
import com.job.androidprojet.data.online.OnlineMusicSearchState
import com.job.androidprojet.data.online.toPreviewMusic
import com.job.androidprojet.data.preferences.MusicLibraryPreferences
import com.job.androidprojet.data.preferences.PersistedMusicLibraryState
import com.job.androidprojet.data.SampleMusicCatalog
import com.job.androidprojet.model.Music
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
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
    val homePreviewState: OnlineMusicSearchState = OnlineMusicSearchState.Idle,
    val onlineSearchState: OnlineMusicSearchState = OnlineMusicSearchState.Idle,
)

class MusicPlayerViewModel(
    initialMusicList: List<Music> = SampleMusicCatalog.songs,
    initialSearchQuery: String = "",
    initialFilterName: String = MusicContentFilter.Music.name,
    private val onlineMusicRepository: OnlineMusicRepository? = null,
    private val musicLibraryPreferences: MusicLibraryPreferences? = null,
) : ViewModel() {
    private var musicList: List<Music> = initialMusicList
    private var searchQuery: String = initialSearchQuery
    private var selectedFilter: MusicContentFilter = MusicContentFilter.fromName(initialFilterName)
    private var selectedMusicId: Long? = null
    private var isPlaying: Boolean = false
    private var playbackProgress: Float = 0f
    private var recentMusicIds: List<Long> = emptyList()
    private var onlinePreviewQueue: List<Music> = emptyList()
    private var homePreviewState: OnlineMusicSearchState = OnlineMusicSearchState.Idle
    private var onlineSearchState: OnlineMusicSearchState = OnlineMusicSearchState.Idle
    private val onlineSearchController = OnlineMusicSearchController(
        repository = onlineMusicRepository,
        scope = viewModelScope,
        onStateChanged = { state ->
            onlineSearchState = state
            emitState()
        },
    )

    private val _uiState = MutableStateFlow(buildUiState())
    val uiState: StateFlow<MusicPlayerUiState> = _uiState.asStateFlow()

    init {
        observePersistedLibraryState()
        loadHomePreviewRecommendations()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        onlineSearchController.search(query)
        emitState()
    }

    fun retryOnlineSearch() {
        onlineSearchController.search(searchQuery)
    }

    fun retryHomePreviewRecommendations() {
        loadHomePreviewRecommendations()
    }

    fun selectFilter(filter: MusicContentFilter) {
        selectedFilter = filter
        emitState()
    }

    fun selectMusic(music: Music) {
        val selectedMusic = playableMusic().firstOrNull { track -> track.id == music.id } ?: return
        selectedMusicId = selectedMusic.id
        isPlaying = true
        playbackProgress = 0f
        recordRecentSelection(selectedMusic.id)
        if (!selectedMusic.isOnlinePreview) {
            persistRecentMusic()
        }
        emitState()
    }

    fun selectOnlinePreview(
        result: OnlineMusicResult,
        recommendations: List<OnlineMusicResult>,
    ) {
        val selectedPreview = result.toPreviewMusic() ?: return
        val previewQueue = recommendations
            .mapNotNull { recommendation -> recommendation.toPreviewMusic() }
            .ifEmpty { listOf(selectedPreview) }
            .distinctBy { music -> music.id }
        onlinePreviewQueue = if (previewQueue.any { music -> music.id == selectedPreview.id }) {
            previewQueue
        } else {
            listOf(selectedPreview) + previewQueue
        }
        selectedMusicId = selectedPreview.id
        isPlaying = true
        playbackProgress = 0f
        recordRecentSelection(selectedPreview.id)
        emitState()
    }

    fun togglePlay() {
        if (musicList.isEmpty()) return
        if (currentMusic() == null) {
            val firstMusic = musicList.first()
            selectedMusicId = firstMusic.id
            isPlaying = true
            playbackProgress = 0f
            recordRecentSelection(firstMusic.id)
            persistRecentMusic()
            emitState()
            return
        }
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
        playbackMusic: Music? = null,
    ) {
        if (playbackMusic?.isOnlinePreview == true &&
            onlinePreviewQueue.none { music -> music.id == playbackMusic.id }
        ) {
            onlinePreviewQueue = listOf(playbackMusic)
        }
        if (musicId != null && playableMusic().any { music -> music.id == musicId }) {
            selectedMusicId = musicId
        }
        this.isPlaying = isPlaying
        playbackProgress = progress.coerceIn(0f, 1f)
        emitState()
    }

    fun toggleFavorite() {
        val currentMusic = currentMusic() ?: return
        if (currentMusic.isOnlinePreview) return
        musicList = musicList.map { music ->
            if (music.id == currentMusic.id) {
                music.copy(isFavorite = !music.isFavorite)
            } else {
                music
            }
        }
        persistFavoriteMusic()
        emitState()
    }

    private fun selectRelativeTrack(offset: Int) {
        val queue = currentPlaybackQueue()
        if (queue.isEmpty()) return
        val currentIndex = queue.indexOfFirst { music -> music.id == currentMusic()?.id }
            .takeIf { index -> index >= 0 }
            ?: 0
        val nextIndex = floorMod(currentIndex + offset, queue.size)
        val nextMusic = queue[nextIndex]
        selectedMusicId = nextMusic.id
        isPlaying = true
        playbackProgress = 0f
        recordRecentSelection(nextMusic.id)
        if (!nextMusic.isOnlinePreview) {
            persistRecentMusic()
        }
        emitState()
    }

    private fun emitState() {
        selectedMusicId = selectedMusicId?.let { id ->
            playableMusic().firstOrNull { music -> music.id == id }?.id
        }
        recentMusicIds = recentMusicIds.filter { id ->
            playableMusic().any { music -> music.id == id }
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
                playableMusic().firstOrNull { music -> music.id == recentId }
            },
            currentMusic = currentMusic,
            isPlaying = isPlaying,
            playbackProgress = playbackProgress,
            currentPositionMillis = currentPositionMillis,
            queuePreview = nextTracks(
                musicList = currentPlaybackQueue(),
                currentMusic = currentMusic,
                count = 3,
            ),
            homePreviewState = homePreviewState,
            onlineSearchState = onlineSearchState,
        )
    }

    private fun loadHomePreviewRecommendations() {
        val repository = onlineMusicRepository ?: return
        homePreviewState = OnlineMusicSearchState.Loading
        emitState()
        viewModelScope.launch {
            val queryResults = HOME_PREVIEW_QUERIES.map { query ->
                repository.searchSafely(query)
            }
            val previewResults = queryResults
                .mapNotNull { result -> result.getOrNull() }
                .flatten()
                .filter { result -> result.previewUrl.isValidPreviewUrl() }
                .distinctBy { result ->
                    "${result.title.lowercase()}-${result.artist.lowercase()}"
                }
                .take(HOME_PREVIEW_COUNT)
            val hasSuccessfulQuery = queryResults.any { result -> result.isSuccess }

            homePreviewState = if (previewResults.isNotEmpty() || hasSuccessfulQuery) {
                OnlineMusicSearchState.Success(previewResults)
            } else {
                OnlineMusicSearchState.Error(
                    message = "Home API previews failed to load. Check the network, then try again. Local sample tracks still work offline.",
                )
            }
            emitState()
        }
    }

    private fun currentMusic(): Music? {
        val selectedId = selectedMusicId ?: return null
        return playableMusic().firstOrNull { music -> music.id == selectedId }
    }

    private fun playableMusic(): List<Music> {
        return (musicList + onlinePreviewQueue).distinctBy { music -> music.id }
    }

    private fun currentPlaybackQueue(): List<Music> {
        val currentMusic = currentMusic()
        return if (currentMusic?.isOnlinePreview == true && onlinePreviewQueue.isNotEmpty()) {
            onlinePreviewQueue
        } else {
            musicList
        }
    }

    private fun observePersistedLibraryState() {
        val preferences = musicLibraryPreferences ?: return
        viewModelScope.launch {
            preferences.state.collect { persistedState ->
                applyPersistedLibraryState(persistedState)
                emitState()
            }
        }
    }

    private fun applyPersistedLibraryState(persistedState: PersistedMusicLibraryState) {
        persistedState.favoriteMusicIds?.let { favoriteMusicIds ->
            musicList = musicList.map { music ->
                music.copy(isFavorite = music.id in favoriteMusicIds)
            }
        }
        recentMusicIds = persistedState.recentMusicIds
            .distinct()
            .filter { id -> musicList.any { music -> music.id == id } }
            .take(MAX_RECENT_MUSIC_COUNT)
    }

    private fun recordRecentSelection(musicId: Long) {
        recentMusicIds = (listOf(musicId) + recentMusicIds.filterNot { id -> id == musicId })
            .take(MAX_RECENT_MUSIC_COUNT)
    }

    private fun persistFavoriteMusic() {
        val preferences = musicLibraryPreferences ?: return
        val favoriteMusicIds = musicList
            .filter { music -> music.isFavorite }
            .map { music -> music.id }
            .toSet()
        viewModelScope.launch {
            preferences.saveFavoriteMusicIds(favoriteMusicIds)
        }
    }

    private fun persistRecentMusic() {
        val preferences = musicLibraryPreferences ?: return
        viewModelScope.launch {
            preferences.saveRecentMusicIds(recentMusicIds)
        }
    }

    companion object {
        private const val MAX_RECENT_MUSIC_COUNT = 8
        private val HOME_PREVIEW_QUERIES = listOf(
            "K-pop",
            "Korean pop",
            "Bollywood",
            "Hindi pop",
            "Indian pop",
        )
        private const val HOME_PREVIEW_COUNT = 8

        fun factory(
            musicList: List<Music>,
            initialSearchQuery: String = "",
            initialFilterName: String = MusicContentFilter.Music.name,
            onlineMusicRepository: OnlineMusicRepository? = null,
            musicLibraryPreferences: MusicLibraryPreferences? = null,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MusicPlayerViewModel::class.java)) {
                        return MusicPlayerViewModel(
                            initialMusicList = musicList,
                            initialSearchQuery = initialSearchQuery,
                            initialFilterName = initialFilterName,
                            onlineMusicRepository = onlineMusicRepository,
                            musicLibraryPreferences = musicLibraryPreferences,
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

private fun String?.isValidPreviewUrl(): Boolean {
    return this?.startsWith(prefix = "https://", ignoreCase = true) == true
}

private suspend fun OnlineMusicRepository.searchSafely(
    query: String,
): Result<List<OnlineMusicResult>> {
    return try {
        search(query)
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        Result.failure(error)
    }
}
