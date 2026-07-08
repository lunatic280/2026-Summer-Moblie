package com.job.androidprojet.playback

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.job.androidprojet.data.SampleMusicCatalog
import com.job.androidprojet.data.online.OnlineMusicResult
import com.job.androidprojet.data.online.toPreviewMusic
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
    val currentMusic: Music? = null,
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
    private val playableMusic = SampleMusicCatalog.songs
    private var activeQueue: List<Music> = playableMusic
    private var pendingControllerAction: ((MediaController) -> Unit)? = null
    private var playbackErrorMessage: String? = null

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
            currentMusic = mediaItem?.mediaId
                ?.toLongOrNull()
                ?.let(::musicById)
            playbackErrorMessage = null
            emitPlaybackState()
        }

        override fun onPlayerError(error: PlaybackException) {
            playbackErrorMessage = if (currentMusic?.isOnlinePreview == true) {
                "This 30-second API preview could not be played. Check the network or choose another preview."
            } else {
                "This local sample could not be played. Choose another track and try again."
            }
            emitPlaybackState()
        }
    }

    init {
        connectController()
        startProgressUpdates()
    }

    fun play(music: Music) {
        val queue = if (music.isOnlinePreview) {
            activeQueue.takeIf { currentQueue ->
                currentQueue.any { queuedMusic -> queuedMusic.id == music.id }
            } ?: listOf(music)
        } else {
            playableMusic
        }
        play(music = music, queue = queue)
    }

    fun playPreview(
        result: OnlineMusicResult,
        recommendations: List<OnlineMusicResult>,
    ) {
        val selectedPreview = result.toPreviewMusic()
        if (selectedPreview == null) {
            playbackErrorMessage =
                "No playable 30-second API preview is available for ${result.title}. This result is metadata only."
            emitPlaybackState()
            return
        }

        val previewQueue = recommendations
            .mapNotNull { recommendation -> recommendation.toPreviewMusic() }
            .ifEmpty { listOf(selectedPreview) }
            .distinctBy { music -> music.id }
        val queue = if (previewQueue.any { music -> music.id == selectedPreview.id }) {
            previewQueue
        } else {
            listOf(selectedPreview) + previewQueue
        }

        play(music = selectedPreview, queue = queue)
    }

    private fun play(
        music: Music,
        queue: List<Music>,
    ) {
        val mediaItem = music.toPlaybackMediaItem(appContext)
        if (mediaItem == null) {
            currentMusic = music
            controller?.stop()
            playbackErrorMessage =
                "Missing local audio resource for ${music.title}. Choose another sample track."
            emitPlaybackState()
            return
        }

        playbackErrorMessage = null
        currentMusic = music
        activeQueue = queue.ifEmpty { listOf(music) }
        withController { player ->
            val mediaItems = activeQueue.mapNotNull { queuedMusic ->
                queuedMusic.toPlaybackMediaItem(appContext)
            }
            val startIndex = mediaItems.indexOfFirst { item ->
                item.mediaId == mediaItem.mediaId
            }.takeIf { index -> index >= 0 } ?: 0

            if (mediaItems.isNotEmpty()) {
                player.setMediaItems(mediaItems, startIndex, 0L)
            } else {
                player.setMediaItem(mediaItem)
            }
            player.prepare()
            player.play()
            emitPlaybackState()
        }
    }

    fun previousTrack() {
        withController { player ->
            if (player.mediaItemCount == 0) return@withController
            val previousIndex = if (player.currentMediaItemIndex > 0) {
                player.currentMediaItemIndex - 1
            } else {
                player.mediaItemCount - 1
            }
            player.seekTo(previousIndex, 0L)
            player.prepare()
            player.play()
            emitPlaybackState()
        }
    }

    fun nextTrack() {
        withController { player ->
            if (player.mediaItemCount == 0) return@withController
            val nextIndex = if (player.currentMediaItemIndex < player.mediaItemCount - 1) {
                player.currentMediaItemIndex + 1
            } else {
                0
            }
            player.seekTo(nextIndex, 0L)
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

    fun pause() {
        withController { player ->
            player.pause()
            emitPlaybackState()
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
        val currentMusicId = mediaId ?: currentMusic?.id
        val currentMusicFromPlayer = mediaId?.let(::musicById)
            ?: player?.currentMediaItem?.toOnlinePreviewMusic()
            ?: currentMusic
        val duration = if (currentMusicFromPlayer?.isOnlinePreview == true) {
            currentMusicFromPlayer.durationMillis
        } else {
            player?.duration?.takeIf { duration -> duration > 0L }
                ?: currentMusicFromPlayer?.durationMillis
                ?: 0L
        }
        val position = player?.currentPosition?.coerceAtLeast(0L) ?: 0L

        _playbackState.value = LocalPlaybackState(
            currentMusicId = currentMusicId,
            currentMusic = currentMusicFromPlayer,
            isPlaying = player?.isPlaying == true,
            positionMillis = position.coerceAtMost(duration),
            durationMillis = duration,
            errorMessage = playbackErrorMessage,
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

    private fun musicById(musicId: Long): Music? {
        return activeQueue.firstOrNull { music -> music.id == musicId }
            ?: playableMusic.firstOrNull { music -> music.id == musicId }
    }

    private companion object {
        const val PROGRESS_UPDATE_INTERVAL_MILLIS = 500L
    }
}

private const val ONLINE_PREVIEW_DURATION_MILLIS = 30_000L

private fun MediaItem.toOnlinePreviewMusic(): Music? {
    val previewMusicId = mediaId.toLongOrNull() ?: return null
    val previewUrl = localConfiguration
        ?.uri
        ?.toString()
        ?.takeIf { url -> url.startsWith(prefix = "https://", ignoreCase = true) }
        ?: return null
    val metadata = mediaMetadata

    return Music(
        id = previewMusicId,
        title = metadata.title?.toString() ?: "30s preview",
        artist = metadata.artist?.toString() ?: "API preview",
        album = metadata.albumTitle?.toString() ?: "Online preview",
        albumImage = metadata.artworkUri?.toString() ?: "online_preview",
        fileName = previewUrl,
        durationMillis = ONLINE_PREVIEW_DURATION_MILLIS,
        previewUrl = previewUrl,
        artworkUrl = metadata.artworkUri?.toString(),
        sourceLabel = "API preview",
        isOnlinePreview = true,
    )
}
