package com.job.androidprojet.playback

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import com.job.androidprojet.data.online.OnlineMusicResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PreviewPlaybackState(
    val currentPreviewId: String? = null,
    val currentPreview: OnlineMusicResult? = null,
    val isPlaying: Boolean = false,
    val positionMillis: Long = 0L,
    val errorMessage: String? = null,
)

class PreviewPlaybackController(
    context: Context,
) : DefaultLifecycleObserver {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var player: ExoPlayer? = null
    private var progressJob: Job? = null
    private var currentPreviewId: String? = null
    private var currentPreview: OnlineMusicResult? = null
    private var previewQueue: List<OnlineMusicResult> = emptyList()

    private val _previewState = MutableStateFlow(PreviewPlaybackState())
    val previewState: StateFlow<PreviewPlaybackState> = _previewState.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            emitState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                playNextPreviewOrStop()
            } else {
                emitState()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            playNextPreviewOrStop()
        }
    }

    fun togglePreview(
        result: OnlineMusicResult,
        recommendations: List<OnlineMusicResult>,
    ) {
        val currentPlayer = player ?: createPlayer()
        if (currentPreviewId == result.id && currentPlayer.isPlaying) {
            stop()
            return
        }

        previewQueue = recommendations.validPreviewResults()
        playPreview(result)
    }

    private fun playPreview(result: OnlineMusicResult) {
        val previewUrl = result.previewUrl
        if (!previewUrl.isValidPreviewUrl()) {
            playNextPreviewOrStop(
                errorMessage = "No iTunes preview is available for ${result.title}",
            )
            return
        }

        val currentPlayer = player ?: createPlayer()
        currentPreviewId = result.id
        currentPreview = result
        currentPlayer.stop()
        currentPlayer.clearMediaItems()
        currentPlayer.setMediaItem(
            MediaItem.Builder()
                .setMediaId(result.id)
                .setUri(previewUrl)
                .build(),
        )
        currentPlayer.prepare()
        currentPlayer.play()
        startProgressUpdates()
        emitState()
    }

    fun stop() {
        progressJob?.cancel()
        progressJob = null
        player?.stop()
        _previewState.value = PreviewPlaybackState()
        currentPreviewId = null
        currentPreview = null
        previewQueue = emptyList()
    }

    override fun onStop(owner: LifecycleOwner) {
        stop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    fun release() {
        progressJob?.cancel()
        progressJob = null
        player?.removeListener(listener)
        player?.release()
        player = null
        currentPreviewId = null
        currentPreview = null
        previewQueue = emptyList()
        _previewState.value = PreviewPlaybackState()
    }

    private fun createPlayer(): ExoPlayer {
        return ExoPlayer.Builder(appContext)
            .build()
            .also { exoPlayer ->
                exoPlayer.addListener(listener)
                player = exoPlayer
            }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val currentPlayer = player
                if (currentPlayer != null &&
                    currentPlayer.currentPosition >= MAX_PREVIEW_DURATION_MILLIS
                ) {
                    playNextPreviewOrStop()
                    return@launch
                }
                emitState()
                delay(PROGRESS_UPDATE_INTERVAL_MILLIS)
            }
        }
    }

    private fun emitState() {
        val currentPlayer = player
        _previewState.value = PreviewPlaybackState(
            currentPreviewId = currentPreviewId,
            currentPreview = currentPreview,
            isPlaying = currentPlayer?.isPlaying == true,
            positionMillis = currentPlayer?.currentPosition?.coerceAtLeast(0L) ?: 0L,
        )
    }

    private fun playNextPreviewOrStop(errorMessage: String? = null) {
        progressJob?.cancel()
        progressJob = null

        val nextPreview = nextPreviewResult()
        if (nextPreview == null) {
            val previousPreviewId = currentPreviewId
            player?.stop()
            currentPreviewId = null
            currentPreview = null
            _previewState.value = PreviewPlaybackState(
                currentPreviewId = previousPreviewId,
                errorMessage = errorMessage,
            )
            return
        }

        playPreview(nextPreview)
    }

    private fun nextPreviewResult(): OnlineMusicResult? {
        if (previewQueue.isEmpty()) return null
        val currentIndex = previewQueue.indexOfFirst { result -> result.id == currentPreviewId }
        val nextIndex = if (currentIndex >= 0) {
            (currentIndex + 1) % previewQueue.size
        } else {
            0
        }
        return previewQueue[nextIndex]
    }

    private companion object {
        const val MAX_PREVIEW_DURATION_MILLIS = 30_000L
        const val PROGRESS_UPDATE_INTERVAL_MILLIS = 500L
    }
}

private fun String?.isValidPreviewUrl(): Boolean {
    return this?.startsWith(prefix = "https://", ignoreCase = true) == true
}

private fun List<OnlineMusicResult>.validPreviewResults(): List<OnlineMusicResult> {
    return filter { result -> result.previewUrl.isValidPreviewUrl() }
}
