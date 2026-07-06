package com.job.androidprojet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.job.androidprojet.data.SampleMusicCatalog
import com.job.androidprojet.playback.LocalPlaybackController
import com.job.androidprojet.ui.home.HomeScreen
import com.job.androidprojet.ui.theme.AndroidProjetTheme

class MainActivity : ComponentActivity() {
    private lateinit var playbackController: LocalPlaybackController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playbackController = LocalPlaybackController(this)
        lifecycle.addObserver(playbackController)

        enableEdgeToEdge()
        setContent {
            val playbackState by playbackController.playbackState.collectAsStateWithLifecycle()

            AndroidProjetTheme(
                darkTheme = true,
                dynamicColor = false,
            ) {
                HomeScreen(
                    musicList = SampleMusicCatalog.songs,
                    onMusicClick = playbackController::play,
                    playbackMusicId = playbackState.currentMusicId,
                    playbackIsPlaying = playbackState.isPlaying,
                    playbackProgress = playbackState.progress,
                    onPlaybackToggle = playbackController::setPlaying,
                    onPlaybackProgressChange = playbackController::seekToProgress,
                    onPreviousTrack = playbackController::play,
                    onNextTrack = playbackController::play,
                )
            }
        }
    }
}
