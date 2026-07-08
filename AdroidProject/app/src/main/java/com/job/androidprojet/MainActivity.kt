package com.job.androidprojet

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.job.androidprojet.data.SampleMusicCatalog
import com.job.androidprojet.data.online.CombinedOnlineMusicRepository
import com.job.androidprojet.data.preferences.DataStoreMusicLibraryPreferences
import com.job.androidprojet.playback.LocalPlaybackController
import com.job.androidprojet.ui.home.HomeDestination
import com.job.androidprojet.ui.home.HomeScreen
import com.job.androidprojet.ui.theme.AndroidProjetTheme
import com.job.androidprojet.widget.MusicPlayerWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private lateinit var playbackController: LocalPlaybackController
    private val requestedDestination = MutableStateFlow<HomeDestination>(HomeDestination.Home)
    private val onlineMusicRepository by lazy {
        CombinedOnlineMusicRepository()
    }
    private val musicLibraryPreferences by lazy {
        DataStoreMusicLibraryPreferences(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedDestination.value = destinationFromIntent(intent)
        playbackController = LocalPlaybackController(this)
        lifecycle.addObserver(playbackController)
        MusicPlayerWidgetProvider.updateAllWidgets(this)

        enableEdgeToEdge()
        setContent {
            val playbackState by playbackController.playbackState.collectAsStateWithLifecycle()
            val initialDestination by requestedDestination.collectAsStateWithLifecycle()

            AndroidProjetTheme(
                darkTheme = true,
                dynamicColor = false,
            ) {
                key(initialDestination) {
                    HomeScreen(
                        musicList = SampleMusicCatalog.songs,
                        onMusicClick = { music ->
                            playbackController.play(music)
                        },
                        playbackMusic = playbackState.currentMusic,
                        playbackMusicId = playbackState.currentMusicId,
                        playbackIsPlaying = playbackState.isPlaying,
                        playbackProgress = playbackState.progress,
                        playbackErrorMessage = playbackState.errorMessage,
                        onPlaybackToggle = { music, shouldPlay ->
                            playbackController.setPlaying(music, shouldPlay)
                        },
                        onPlaybackProgressChange = playbackController::seekToProgress,
                        onPreviousTrack = playbackController::previousTrack,
                        onNextTrack = playbackController::nextTrack,
                        onPreviewToggle = { result, recommendations ->
                            playbackController.playPreview(
                                result = result,
                                recommendations = recommendations,
                            )
                        },
                        initialDestination = initialDestination,
                        onlineMusicRepository = onlineMusicRepository,
                        musicLibraryPreferences = musicLibraryPreferences,
                        canPinMusicWidget = canPinMusicWidget(),
                        onPinMusicWidgetClick = ::requestPinMusicWidget,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        requestedDestination.value = destinationFromIntent(intent)
    }

    private fun destinationFromIntent(intent: Intent?): HomeDestination {
        return when (intent?.getStringExtra(EXTRA_START_DESTINATION)) {
            DESTINATION_SEARCH -> HomeDestination.Search
            else -> HomeDestination.Home
        }
    }

    private fun canPinMusicWidget(): Boolean {
        return AppWidgetManager.getInstance(this).isRequestPinAppWidgetSupported
    }

    private fun requestPinMusicWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        if (!appWidgetManager.isRequestPinAppWidgetSupported) return

        MusicPlayerWidgetProvider.updateAllWidgets(this)

        val provider = ComponentName(this, MusicPlayerWidgetProvider::class.java)
        val successCallback = PendingIntent.getActivity(
            this,
            PIN_WIDGET_REQUEST_CODE,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        appWidgetManager.requestPinAppWidget(provider, null, successCallback)
    }

    companion object {
        const val EXTRA_START_DESTINATION = "com.job.androidprojet.extra.START_DESTINATION"
        const val DESTINATION_SEARCH = "search"
        private const val PIN_WIDGET_REQUEST_CODE = 2001
    }
}
