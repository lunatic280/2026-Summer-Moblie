package com.job.androidprojet.playback

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.job.androidprojet.MainActivity
import com.job.androidprojet.R
import com.job.androidprojet.data.SampleMusicCatalog
import com.job.androidprojet.model.Music
import com.job.androidprojet.widget.MusicPlayerWidgetProvider
import com.job.androidprojet.widget.MusicWidgetState
import com.job.androidprojet.widget.MusicWidgetStateStore

@OptIn(UnstableApi::class)
class MusicPlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var playbackPlayer: ExoPlayer? = null
    private val previewLimitHandler = Handler(Looper.getMainLooper())
    private val previewLimitRunnable = object : Runnable {
        override fun run() {
            val player = playbackPlayer ?: return
            if (!player.isPlaying || !player.isCurrentMediaItemApiPreview()) return

            if (player.currentPosition >= ONLINE_PREVIEW_DURATION_MILLIS) {
                stopOrAdvancePreview(player)
            } else {
                previewLimitHandler.postDelayed(this, PREVIEW_LIMIT_CHECK_INTERVAL_MILLIS)
            }
        }
    }
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            syncPreviewLimitTimer()
            updateWidgetState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            syncPreviewLimitTimer()
            updateWidgetState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            syncPreviewLimitTimer()
            updateWidgetState()
        }
    }

    override fun onCreate() {
        super.onCreate()
        setMediaNotificationProvider(MusicNotificationProvider(this))

        val player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            setHandleAudioBecomingNoisy(true)
            repeatMode = Player.REPEAT_MODE_OFF
            addListener(playerListener)
        }
        playbackPlayer = player
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(createSessionActivityPendingIntent())
            .setMediaButtonPreferences(mediaButtonPreferences())
            .build()
            .also(::addSession)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        val widgetCommandOutcome = when (intent?.action) {
            ACTION_WIDGET_PREVIOUS -> handlePreviousCommand()
            ACTION_WIDGET_PLAY_PAUSE -> handlePlayPauseCommand(intent)
            ACTION_WIDGET_NEXT -> handleNextCommand()
            ACTION_WIDGET_PLAY_TRACK -> handlePlayTrackCommand(intent)
            else -> null
        }
        if (widgetCommandOutcome == WidgetCommandOutcome.StopService) {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        return result
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo,
    ): MediaSession? = mediaSession

    override fun onDestroy() {
        previewLimitHandler.removeCallbacks(previewLimitRunnable)
        playbackPlayer?.removeListener(playerListener)
        playbackPlayer?.let { player ->
            saveWidgetState(player = player, isPlaying = false)
            MusicPlayerWidgetProvider.updateAllWidgets(this)
        }
        playbackPlayer = null
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }

    private fun createSessionActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            this,
            SESSION_ACTIVITY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun mediaButtonPreferences(): List<CommandButton> {
        return listOf(
            CommandButton.Builder(CommandButton.ICON_PREVIOUS)
                .setDisplayName("Previous track")
                .setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                .setSlots(CommandButton.SLOT_BACK)
                .build(),
            CommandButton.Builder(CommandButton.ICON_NEXT)
                .setDisplayName("Next track")
                .setPlayerCommand(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                .setSlots(CommandButton.SLOT_FORWARD)
                .build(),
        )
    }

    private fun handlePreviousCommand(): WidgetCommandOutcome {
        val player = playbackPlayer ?: return stopWidgetPlaybackState()
        if (!ensureQueuePrepared(player)) return stopWidgetPlaybackState()

        val previousIndex = if (player.currentMediaItemIndex > 0) {
            player.currentMediaItemIndex - 1
        } else {
            player.mediaItemCount - 1
        }
        player.seekTo(previousIndex, 0L)
        player.prepare()
        player.play()
        triggerNotificationUpdate()
        updateWidgetState()
        return WidgetCommandOutcome.KeepService
    }

    private fun handlePlayPauseCommand(intent: Intent?): WidgetCommandOutcome {
        val player = playbackPlayer ?: return stopWidgetPlaybackState()
        val requestedShouldPlay = intent
            ?.takeIf { command -> command.hasExtra(EXTRA_WIDGET_SHOULD_PLAY) }
            ?.getBooleanExtra(EXTRA_WIDGET_SHOULD_PLAY, false)

        if (requestedShouldPlay == false) {
            player.pause()
            if (player.currentMediaItem != null) {
                updateWidgetState()
            } else {
                MusicWidgetStateStore.setPlaying(this, isPlaying = false)
                MusicPlayerWidgetProvider.updateAllWidgets(this)
            }
            return WidgetCommandOutcome.StopService
        }

        if (!ensureQueuePrepared(player)) return stopWidgetPlaybackState()

        if (requestedShouldPlay == true) {
            if (player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            player.play()
            triggerNotificationUpdate()
        } else {
            if (player.isPlaying) {
                player.pause()
                updateWidgetState()
                return WidgetCommandOutcome.StopService
            } else if (player.playbackState == Player.STATE_IDLE) {
                player.prepare()
                player.play()
                triggerNotificationUpdate()
            } else {
                player.play()
                triggerNotificationUpdate()
            }
        }
        updateWidgetState()
        return WidgetCommandOutcome.KeepService
    }

    private fun handleNextCommand(): WidgetCommandOutcome {
        val player = playbackPlayer ?: return stopWidgetPlaybackState()
        if (!ensureQueuePrepared(player)) return stopWidgetPlaybackState()

        val nextIndex = if (player.currentMediaItemIndex < player.mediaItemCount - 1) {
            player.currentMediaItemIndex + 1
        } else {
            0
        }
        player.seekTo(nextIndex, 0L)
        player.prepare()
        player.play()
        triggerNotificationUpdate()
        updateWidgetState()
        return WidgetCommandOutcome.KeepService
    }

    private fun handlePlayTrackCommand(intent: Intent?): WidgetCommandOutcome {
        val musicId = intent?.getLongExtra(EXTRA_WIDGET_MUSIC_ID, NO_WIDGET_MUSIC_ID)
            ?: NO_WIDGET_MUSIC_ID
        if (musicId == NO_WIDGET_MUSIC_ID) return stopWidgetPlaybackState()

        val player = playbackPlayer ?: return stopWidgetPlaybackState()
        if (!prepareLocalQueue(
                player = player,
                startMusicId = musicId,
                requireStartMusicId = true,
            )
        ) {
            return stopWidgetPlaybackState()
        }

        player.prepare()
        player.play()
        triggerNotificationUpdate()
        updateWidgetState()
        return WidgetCommandOutcome.KeepService
    }

    private fun ensureQueuePrepared(player: Player): Boolean {
        if (player.mediaItemCount > 0) return true

        val storedState = MusicWidgetStateStore.current(this)
        if (storedState.previewUrl != null) {
            val previewItem = storedState.toRestoredPreviewMusic()
                ?.toPlaybackMediaItem(this)
                ?: return false
            player.setMediaItem(previewItem)
            return true
        }

        return prepareLocalQueue(player, startMusicId = storedState.musicId)
    }

    private fun prepareLocalQueue(
        player: Player,
        startMusicId: Long?,
        requireStartMusicId: Boolean = false,
    ): Boolean {
        val queue = SampleMusicCatalog.songs
        val mediaItems = queue.mapNotNull { music ->
            music.toPlaybackMediaItem(this)
        }
        if (mediaItems.isEmpty()) return false

        val requestedStartIndex = startMusicId?.let { musicId ->
            mediaItems.indexOfFirst { item -> item.mediaId == musicId.toString() }
        }
        if (requireStartMusicId && (requestedStartIndex == null || requestedStartIndex < 0)) {
            return false
        }
        val startIndex = requestedStartIndex
            ?.takeIf { index -> index >= 0 }
            ?: 0

        player.setMediaItems(mediaItems, startIndex, 0L)
        return true
    }

    private fun MusicWidgetState.toRestoredPreviewMusic(): Music? {
        val previewUrl = this.previewUrl
            ?.takeIf { url -> url.startsWith(prefix = "https://", ignoreCase = true) }
            ?: return null

        return Music(
            id = musicId ?: restoredPreviewMusicId(previewUrl),
            title = title,
            artist = artist,
            album = album,
            albumImage = artworkUrl ?: "online_preview",
            fileName = previewUrl,
            durationMillis = ONLINE_PREVIEW_DURATION_MILLIS,
            previewUrl = previewUrl,
            artworkUrl = artworkUrl,
            sourceLabel = "API preview",
            isOnlinePreview = true,
        )
    }

    private fun restoredPreviewMusicId(previewUrl: String): Long {
        val stableHash = previewUrl.hashCode().toLong() and 0x00000000FFFFFFFFL
        return RESTORED_PREVIEW_ID_BASE - stableHash
    }

    private fun stopWidgetPlaybackState(): WidgetCommandOutcome {
        MusicWidgetStateStore.setPlaying(this, isPlaying = false)
        MusicPlayerWidgetProvider.updateAllWidgets(this)
        return WidgetCommandOutcome.StopService
    }

    private fun syncPreviewLimitTimer() {
        previewLimitHandler.removeCallbacks(previewLimitRunnable)
        val player = playbackPlayer ?: return
        if (player.isPlaying && player.isCurrentMediaItemApiPreview()) {
            previewLimitHandler.postDelayed(
                previewLimitRunnable,
                PREVIEW_LIMIT_CHECK_INTERVAL_MILLIS,
            )
        }
    }

    private fun stopOrAdvancePreview(player: Player) {
        if (!player.isCurrentMediaItemApiPreview()) return

        val hasNextPreview = player.currentMediaItemIndex < player.mediaItemCount - 1
        if (hasNextPreview) {
            player.seekTo(player.currentMediaItemIndex + 1, 0L)
            player.prepare()
            player.play()
        } else {
            player.pause()
            player.seekTo(0L)
        }
        updateWidgetState()
        syncPreviewLimitTimer()
    }

    private fun updateWidgetState() {
        val player = playbackPlayer ?: return
        saveWidgetState(player = player, isPlaying = player.isPlaying)
        MusicPlayerWidgetProvider.updateAllWidgets(this)
    }

    private fun saveWidgetState(player: Player, isPlaying: Boolean) {
        val mediaItem = player.currentMediaItem ?: return
        val musicId = mediaItem.mediaId.toLongOrNull() ?: return
        val localMusic = SampleMusicCatalog.songs.firstOrNull { candidate ->
            candidate.id == musicId
        }
        val metadata = mediaItem.mediaMetadata
        val title = localMusic?.title ?: metadata.title?.toString() ?: "30s preview"
        val artist = localMusic?.artist ?: metadata.artist?.toString() ?: "API preview"
        val album = localMusic?.album ?: metadata.albumTitle?.toString() ?: "Online preview"
        val previewUrl = localMusic?.previewUrl ?: mediaItem.localConfiguration
            ?.uri
            ?.toString()
            ?.takeIf { url -> url.startsWith(prefix = "https://", ignoreCase = true) }
        val artworkUrl = localMusic?.artworkUrl ?: metadata.artworkUri?.toString()

        MusicWidgetStateStore.saveCurrentTrack(
            context = this,
            musicId = musicId,
            title = title,
            artist = artist,
            album = album,
            previewUrl = previewUrl,
            artworkUrl = artworkUrl,
            isPlaying = isPlaying,
        )
    }

    companion object {
        const val ACTION_WIDGET_PREVIOUS = "com.job.androidprojet.playback.action.WIDGET_PREVIOUS"
        const val ACTION_WIDGET_PLAY_PAUSE = "com.job.androidprojet.playback.action.WIDGET_PLAY_PAUSE"
        const val ACTION_WIDGET_NEXT = "com.job.androidprojet.playback.action.WIDGET_NEXT"
        const val ACTION_WIDGET_PLAY_TRACK = "com.job.androidprojet.playback.action.WIDGET_PLAY_TRACK"
        const val EXTRA_WIDGET_SHOULD_PLAY = "com.job.androidprojet.playback.extra.WIDGET_SHOULD_PLAY"
        const val EXTRA_WIDGET_MUSIC_ID = "com.job.androidprojet.playback.extra.WIDGET_MUSIC_ID"

        const val PLAYBACK_NOTIFICATION_CHANNEL_ID = "music_playback"
        const val PLAYBACK_NOTIFICATION_ID = 1001
        const val SESSION_ACTIVITY_REQUEST_CODE = 1001
        private const val ONLINE_PREVIEW_DURATION_MILLIS = 30_000L
        private const val PREVIEW_LIMIT_CHECK_INTERVAL_MILLIS = 500L
        private const val RESTORED_PREVIEW_ID_BASE = -2_000_000_000_000L
        private const val NO_WIDGET_MUSIC_ID = Long.MIN_VALUE
    }

    private class MusicNotificationProvider(
        context: Context,
    ) : DefaultMediaNotificationProvider(
        context,
        DefaultMediaNotificationProvider.NotificationIdProvider { PLAYBACK_NOTIFICATION_ID },
        PLAYBACK_NOTIFICATION_CHANNEL_ID,
        R.string.playback_notification_channel_name,
    ) {
        override fun getNotificationContentTitle(metadata: MediaMetadata): CharSequence? {
            return metadata.title ?: metadata.displayTitle
        }

        override fun getNotificationContentText(metadata: MediaMetadata): CharSequence? {
            val contentText = listOfNotNull(
                metadata.artist?.toString()?.takeIf { artist -> artist.isNotBlank() },
                metadata.albumTitle?.toString()?.takeIf { album -> album.isNotBlank() },
            )
                .distinct()
                .joinToString(" - ")

            return contentText.takeIf { text -> text.isNotBlank() }
                ?: metadata.subtitle
                ?: metadata.description
        }
    }

    private enum class WidgetCommandOutcome {
        KeepService,
        StopService,
    }
}

private fun Player.isCurrentMediaItemApiPreview(): Boolean {
    return currentMediaItem
        ?.localConfiguration
        ?.uri
        ?.toString()
        ?.startsWith(prefix = "https://", ignoreCase = true) == true
}
