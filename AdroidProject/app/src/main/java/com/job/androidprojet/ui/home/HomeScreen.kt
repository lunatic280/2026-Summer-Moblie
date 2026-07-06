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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.job.androidprojet.model.Music

@Composable
fun HomeScreen(
    musicList: List<Music>,
    modifier: Modifier = Modifier,
    onMusicClick: ((Music) -> Unit)? = null,
    playbackMusicId: Long? = null,
    playbackIsPlaying: Boolean? = null,
    playbackProgress: Float? = null,
    onPlaybackToggle: ((music: Music?, shouldPlay: Boolean) -> Unit)? = null,
    onPlaybackProgressChange: ((Float) -> Unit)? = null,
    onPreviousTrack: ((Music) -> Unit)? = null,
    onNextTrack: ((Music) -> Unit)? = null,
    initialSearchQuery: String = "",
    initialFilterName: String = "Music",
    initialDestinationName: String = "Home",
) {
    val initialDestination = remember(initialDestinationName) {
        HomeDestination.fromName(initialDestinationName)
    }
    val viewModelFactory = remember(musicList, initialSearchQuery, initialFilterName) {
        MusicPlayerViewModel.factory(
            musicList = musicList,
            initialSearchQuery = initialSearchQuery,
            initialFilterName = initialFilterName,
        )
    }
    val playerViewModel: MusicPlayerViewModel = viewModel(factory = viewModelFactory)
    val uiState by playerViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = HomeDestination.fromRoute(navBackStackEntry?.destination?.route)
        ?: initialDestination

    LaunchedEffect(playbackMusicId, playbackIsPlaying, playbackProgress) {
        if (playbackIsPlaying != null && playbackProgress != null) {
            playerViewModel.syncPlaybackState(
                musicId = playbackMusicId,
                isPlaying = playbackIsPlaying,
                progress = playbackProgress,
            )
        }
    }

    fun navigateTo(destination: HomeDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun selectMusic(music: Music) {
        playerViewModel.selectMusic(music)
        navigateTo(HomeDestination.Player)
        onMusicClick?.invoke(music)
    }

    fun relativeTrack(offset: Int): Music? {
        if (uiState.musicList.isEmpty()) return null
        val currentIndex = uiState.musicList.indexOfFirst { music ->
            music.id == uiState.currentMusic?.id
        }.takeIf { index -> index >= 0 } ?: 0
        val nextIndex = floorMod(currentIndex + offset, uiState.musicList.size)
        return uiState.musicList[nextIndex]
    }

    fun togglePlayback() {
        val shouldPlay = !uiState.isPlaying
        playerViewModel.togglePlay()
        onPlaybackToggle?.invoke(uiState.currentMusic, shouldPlay)
    }

    fun previousTrack() {
        val previousMusic = relativeTrack(offset = -1)
        playerViewModel.previousTrack()
        previousMusic?.let { music -> onPreviousTrack?.invoke(music) }
    }

    fun nextTrack() {
        val nextMusic = relativeTrack(offset = 1)
        playerViewModel.nextTrack()
        nextMusic?.let { music -> onNextTrack?.invoke(music) }
    }

    fun updatePlaybackProgress(progress: Float) {
        playerViewModel.updateProgress(progress)
        onPlaybackProgressChange?.invoke(progress)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = HomeBackground,
        bottomBar = {
            HomeBottomBar(
                currentMusic = uiState.currentMusic,
                isPlaying = uiState.isPlaying,
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
            startDestination = initialDestination.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(HomeDestination.Home.route) {
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
                ) {
                    homeContent(
                        music = uiState.filteredMusic,
                        recentMusic = uiState.recentMusic,
                        onMusicClick = ::selectMusic,
                    )
                }
            }

            composable(HomeDestination.Search.route) {
                MusicRouteContent(
                    trackCount = uiState.musicList.size,
                    searchQuery = uiState.searchQuery,
                    selectedFilter = uiState.selectedFilter,
                    resultCount = uiState.filteredMusic.size,
                    onQueryChange = playerViewModel::updateSearchQuery,
                    onFilterSelected = playerViewModel::selectFilter,
                ) {
                    searchContent(
                        music = uiState.filteredMusic,
                        query = uiState.searchQuery,
                        onMusicClick = ::selectMusic,
                    )
                }
            }

            composable(HomeDestination.Player.route) {
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

            composable(HomeDestination.Library.route) {
                MusicRouteContent(
                    trackCount = uiState.musicList.size,
                    searchQuery = uiState.searchQuery,
                    selectedFilter = uiState.selectedFilter,
                    resultCount = uiState.filteredMusic.size,
                    onQueryChange = playerViewModel::updateSearchQuery,
                    onFilterSelected = playerViewModel::selectFilter,
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

private fun floorMod(value: Int, divisor: Int): Int {
    return ((value % divisor) + divisor) % divisor
}
