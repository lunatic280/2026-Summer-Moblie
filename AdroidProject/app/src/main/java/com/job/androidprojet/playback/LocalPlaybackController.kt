package com.job.androidprojet.playback

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.job.androidprojet.model.Music
import com.google.common.util.concurrent.ListenableFuture
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

data class LocalPlaybackState(
    val currentMusicId: Long? = null,
    val isPlaying: Boolean = false,
    val positionMillis: Long = 0L,
    val durationMillis: Long = 0L,
    val errorMessage: String? = null,
) {
    val progress: Float
        get() = if (durationMillis > 0L) {
            (positionMillis.toFloat() / durationMillis.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
}

class LocalPlaybackController(
    context: Context,
) : DefaultLifecycleObserver {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var progressJob: Job? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var currentMusic: Music? = null
    private var pendingControllerAction: ((MediaController) -> Unit)? = null

    private val _playbackState = MutableStateFlow(LocalPlaybackState())
    val playbackState: StateFlow<LocalPlaybackState> = _playbackState.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            emitPlaybackState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val player = controller ?: return
            if (playbackState == Player.STATE_ENDED) {
                player.pause()
                player.seekTo(0L)
            }
            emitPlaybackState()
        }

        override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
            emitPlaybackState()
        }
    }

    init {
        connectController()
        startProgressUpdates()
    }

    fun play(music: Music) {
        val mediaItem = music.toLocalMediaItem(appContext)
        if (mediaItem == null) {
            currentMusic = music
            controller?.stop()
            _playbackState.value = LocalPlaybackState(
                currentMusicId = music.id,
                errorMessage = "Missing local audio resource for ${music.fileName}",
            )
            return
        }

        currentMusic = music
        withController { player ->
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            emitPlaybackState()
        }
    }

    fun setPlaying(music: Music?, shouldPlay: Boolean) {
        if (shouldPlay) {
            val player = controller
            if (player?.currentMediaItem == null && music != null) {
                play(music)
            } else if (player != null) {
                player.play()
                emitPlaybackState()
            } else {
                music?.let(::play)
            }
        } else {
            withController { player ->
                player.pause()
                emitPlaybackState()
            }
        }
    }

    fun seekToProgress(progress: Float) {
        val player = controller ?: return
        val duration = player.duration.takeIf { duration -> duration > 0L } ?: return
        player.seekTo((duration * progress.coerceIn(0f, 1f)).toLong())
        emitPlaybackState()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    fun release() {
        progressJob?.cancel()
        controller?.removeListener(listener)
        controller = null
        controllerFuture?.let(MediaController::releaseFuture)
        controllerFuture = null
        pendingControllerAction = null
    }

    private fun startProgressUpdates() {
        progressJob = scope.launch {
            while (isActive) {
                emitPlaybackState()
                delay(PROGRESS_UPDATE_INTERVAL_MILLIS)
            }
        }
    }

    private fun emitPlaybackState() {
        val player = controller
        val mediaId = player?.currentMediaItem?.mediaId?.toLongOrNull()
        val currentMusicId = currentMusic?.id ?: mediaId
        val duration = player?.duration?.takeIf { duration -> duration > 0L }
            ?: currentMusic?.durationMillis
            ?: 0L
        val position = player?.currentPosition?.coerceAtLeast(0L) ?: 0L

        _playbackState.value = LocalPlaybackState(
            currentMusicId = currentMusicId,
            isPlaying = player?.isPlaying == true,
            positionMillis = position.coerceAtMost(duration),
            durationMillis = duration,
        )
    }

    private fun connectController() {
        val sessionToken = SessionToken(
            appContext,
            ComponentName(appContext, MusicPlaybackService::class.java),
        )
        controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                val connectedController = controllerFuture?.get() ?: return@addListener
                controller = connectedController
                connectedController.addListener(listener)
                pendingControllerAction?.invoke(connectedController)
                pendingControllerAction = null
                emitPlaybackState()
            },
            appContext.mainExecutor,
        )
    }

    private fun withController(action: (MediaController) -> Unit) {
        val connectedController = controller
        if (connectedController != null) {
            action(connectedController)
        } else {
            pendingControllerAction = action
        }
    }

    private companion object {
        const val PROGRESS_UPDATE_INTERVAL_MILLIS = 500L
    }
}
