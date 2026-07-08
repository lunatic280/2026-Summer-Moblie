package com.job.androidprojet.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.job.androidprojet.data.online.OnlineMusicResult
import com.job.androidprojet.data.online.OnlineMusicRepository
import com.job.androidprojet.data.preferences.MusicLibraryPreferences
import com.job.androidprojet.model.Music

@Composable
internal fun HomeScreen(
    musicList: List<Music>,
    modifier: Modifier = Modifier,
    onMusicClick: ((Music) -> Unit)? = null,
    playbackMusic: Music? = null,
    playbackMusicId: Long? = null,
    playbackIsPlaying: Boolean? = null,
    playbackProgress: Float? = null,
    playbackErrorMessage: String? = null,
    onPlaybackToggle: ((music: Music?, shouldPlay: Boolean) -> Unit)? = null,
    onPlaybackProgressChange: ((Float) -> Unit)? = null,
    onPreviousTrack: (() -> Unit)? = null,
    onNextTrack: (() -> Unit)? = null,
    previewMusicId: String? = null,
    currentPreview: OnlineMusicResult? = null,
    previewIsPlaying: Boolean = false,
    onPreviewToggle: ((OnlineMusicResult, List<OnlineMusicResult>) -> Unit)? = null,
    initialSearchQuery: String = "",
    initialFilterName: String = "Music",
    initialDestination: HomeDestination = HomeDestination.Home,
    onlineMusicRepository: OnlineMusicRepository? = null,
    musicLibraryPreferences: MusicLibraryPreferences? = null,
    canPinMusicWidget: Boolean = false,
    onPinMusicWidgetClick: (() -> Unit)? = null,
) {
    val viewModelFactory = remember(
        musicList,
        initialSearchQuery,
        initialFilterName,
        onlineMusicRepository,
        musicLibraryPreferences,
    ) {
        MusicPlayerViewModel.factory(
            musicList = musicList,
            initialSearchQuery = initialSearchQuery,
            initialFilterName = initialFilterName,
            onlineMusicRepository = onlineMusicRepository,
            musicLibraryPreferences = musicLibraryPreferences,
        )
    }
    val playerViewModel: MusicPlayerViewModel = viewModel(factory = viewModelFactory)
    val uiState by playerViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = HomeDestination.fromNavDestination(navBackStackEntry?.destination)
        ?: initialDestination

    LaunchedEffect(playbackMusic, playbackMusicId, playbackIsPlaying, playbackProgress) {
        if (playbackIsPlaying != null && playbackProgress != null) {
            playerViewModel.syncPlaybackState(
                musicId = playbackMusicId,
                isPlaying = playbackIsPlaying,
                progress = playbackProgress,
                playbackMusic = playbackMusic,
            )
        }
    }

    fun NavOptionsBuilder.configureTopLevelNavigation() {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

    fun navigateTo(destination: HomeDestination) {
        when (destination) {
            HomeDestination.Home -> navController.navigate(HomeDestination.Home) {
                configureTopLevelNavigation()
            }

            HomeDestination.Search -> navController.navigate(HomeDestination.Search) {
                configureTopLevelNavigation()
            }

            HomeDestination.Player -> navController.navigate(HomeDestination.Player) {
                configureTopLevelNavigation()
            }

            HomeDestination.Library -> navController.navigate(HomeDestination.Library) {
                configureTopLevelNavigation()
            }
        }
    }

    fun selectMusic(music: Music) {
        playerViewModel.selectMusic(music)
        navigateTo(HomeDestination.Player)
        onMusicClick?.invoke(music)
    }

    fun selectPreview(
        result: OnlineMusicResult,
        recommendations: List<OnlineMusicResult>,
    ) {
        playerViewModel.selectOnlinePreview(
            result = result,
            recommendations = recommendations,
        )
        navigateTo(HomeDestination.Player)
        onPreviewToggle?.invoke(result, recommendations)
    }

    fun togglePlayback() {
        val shouldPlay = !uiState.isPlaying
        playerViewModel.togglePlay()
        onPlaybackToggle?.invoke(uiState.currentMusic, shouldPlay)
    }

    fun previousTrack() {
        playerViewModel.previousTrack()
        onPreviousTrack?.invoke()
    }

    fun nextTrack() {
        playerViewModel.nextTrack()
        onNextTrack?.invoke()
    }

    fun updatePlaybackProgress(progress: Float) {
        playerViewModel.updateProgress(progress)
        onPlaybackProgressChange?.invoke(progress)
    }

    val activePreviewId = uiState.currentMusic?.onlinePreviewId ?: previewMusicId
    val activePreviewIsPlaying = (uiState.currentMusic?.isOnlinePreview == true &&
        uiState.isPlaying) || previewIsPlaying

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = HomeBackground,
        bottomBar = {
            HomeBottomBar(
                currentMusic = uiState.currentMusic,
                isPlaying = uiState.isPlaying,
                currentPreview = null,
                isPreviewPlaying = activePreviewIsPlaying,
                selectedDestination = selectedDestination,
                onTogglePlay = ::togglePlayback,
                onPrevious = ::previousTrack,
                onNext = ::nextTrack,
                onOpenPlayer = {
                    navigateTo(HomeDestination.Player)
                },
                onDestinationSelected = ::navigateTo,
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = initialDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable<HomeDestination.Home> {
                MusicRouteContent(
                    trackCount = uiState.musicList.size,
                    searchQuery = uiState.searchQuery,
                    selectedFilter = uiState.selectedFilter,
                    resultCount = uiState.filteredMusic.size,
                    onQueryChange = { query ->
                        playerViewModel.updateSearchQuery(query)
                        if (selectedDestination == HomeDestination.Home) {
                            navigateTo(HomeDestination.Search)
                        }
                    },
                    onFilterSelected = playerViewModel::selectFilter,
                    canPinWidget = canPinMusicWidget,
                    onPinWidgetClick = onPinMusicWidgetClick,
                ) {
                    homeContent(
                        music = uiState.filteredMusic,
                        recentMusic = uiState.recentMusic,
                        homePreviewState = uiState.homePreviewState,
                        previewMusicId = activePreviewId,
                        previewIsPlaying = activePreviewIsPlaying,
                        onPreviewToggle = ::selectPreview,
                        onHomePreviewRetry = playerViewModel::retryHomePreviewRecommendations,
                        onMusicClick = ::selectMusic,
                    )
                }
            }

            composable<HomeDestination.Search> {
                MusicRouteContent(
                    trackCount = uiState.musicList.size,
                    searchQuery = uiState.searchQuery,
                    selectedFilter = uiState.selectedFilter,
                    resultCount = uiState.filteredMusic.size,
                    onQueryChange = playerViewModel::updateSearchQuery,
                    onFilterSelected = playerViewModel::selectFilter,
                    canPinWidget = canPinMusicWidget,
                    onPinWidgetClick = onPinMusicWidgetClick,
                ) {
                    searchContent(
                        music = uiState.filteredMusic,
                        query = uiState.searchQuery,
                        onlineSearchState = uiState.onlineSearchState,
                        previewMusicId = activePreviewId,
                        previewIsPlaying = activePreviewIsPlaying,
                        onPreviewToggle = ::selectPreview,
                        onOnlineSearchRetry = playerViewModel::retryOnlineSearch,
                        onMusicClick = ::selectMusic,
                    )
                }
            }

            composable<HomeDestination.Player> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 24.dp,
                        end = 16.dp,
                        bottom = 24.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    item {
                        PlayerDetailScreen(
                            music = uiState.currentMusic,
                            isPlaying = uiState.isPlaying,
                            positionMillis = uiState.currentPositionMillis,
                            progress = uiState.playbackProgress,
                            queuePreview = uiState.queuePreview,
                            errorMessage = playbackErrorMessage,
                            onProgressChange = ::updatePlaybackProgress,
                            onTogglePlay = ::togglePlayback,
                            onPrevious = ::previousTrack,
                            onNext = ::nextTrack,
                            onToggleFavorite = playerViewModel::toggleFavorite,
                            onQueueTrackClick = ::selectMusic,
                        )
                    }
                }
            }

            composable<HomeDestination.Library> {
                MusicRouteContent(
                    trackCount = uiState.musicList.size,
                    searchQuery = uiState.searchQuery,
                    selectedFilter = uiState.selectedFilter,
                    resultCount = uiState.filteredMusic.size,
                    onQueryChange = playerViewModel::updateSearchQuery,
                    onFilterSelected = playerViewModel::selectFilter,
                    canPinWidget = canPinMusicWidget,
                    onPinWidgetClick = onPinMusicWidgetClick,
                ) {
                    libraryContent(
                        music = uiState.filteredMusic,
                        allMusic = uiState.musicList,
                        recentMusic = uiState.recentMusic,
                        onMusicClick = ::selectMusic,
                    )
                }
            }
        }
    }
}
