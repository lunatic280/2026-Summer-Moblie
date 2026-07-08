package com.job.androidprojet.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.job.androidprojet.R
import com.job.androidprojet.playback.MusicPlaybackService

class MusicPlayerWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_WIDGET_PREVIOUS,
            ACTION_WIDGET_PLAY_PAUSE,
            ACTION_WIDGET_NEXT,
            -> handlePlaybackCommand(context, intent)
            ACTION_WIDGET_REFRESH,
            -> updateAllWidgets(context)
        }
    }

    companion object {
        const val ACTION_WIDGET_PREVIOUS = MusicPlaybackService.ACTION_WIDGET_PREVIOUS
        const val ACTION_WIDGET_PLAY_PAUSE = MusicPlaybackService.ACTION_WIDGET_PLAY_PAUSE
        const val ACTION_WIDGET_NEXT = MusicPlaybackService.ACTION_WIDGET_NEXT
        const val ACTION_WIDGET_OPEN_SEARCH = "com.job.androidprojet.widget.action.OPEN_SEARCH"
        const val ACTION_WIDGET_REFRESH = "com.job.androidprojet.widget.action.REFRESH"
        const val EXTRA_OPEN_SEARCH = "com.job.androidprojet.widget.extra.OPEN_SEARCH"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetComponent = ComponentName(context, MusicPlayerWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)

            widgetIds.forEach { appWidgetId ->
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val state = MusicWidgetStateStore.current(context)
            val views = RemoteViews(context.packageName, R.layout.music_player_widget)

            views.setTextViewText(R.id.widget_title, state.title)
            views.setTextViewText(R.id.widget_artist, state.artist)
            views.setTextViewText(
                R.id.widget_status,
                when {
                    state.isPlaying && state.previewUrl != null -> "API preview playing"
                    state.isPlaying -> "Local track playing"
                    state.hasTrack && state.previewUrl != null -> "API preview paused"
                    state.hasTrack -> "Local track paused"
                    else -> "Ready"
                },
            )
            val itemLabel = if (state.previewUrl != null) {
                "API preview clip"
            } else if (state.hasTrack) {
                "local sample track"
            } else {
                "local sample tracks"
            }
            views.setImageViewResource(
                R.id.widget_play_pause,
                if (state.isPlaying) {
                    android.R.drawable.ic_media_pause
                } else {
                    android.R.drawable.ic_media_play
                },
            )
            views.setContentDescription(
                R.id.widget_previous,
                if (state.hasTrack) "Previous $itemLabel" else "Previous local sample track",
            )
            views.setContentDescription(
                R.id.widget_play_pause,
                when {
                    state.isPlaying -> "Pause $itemLabel"
                    else -> "Play $itemLabel"
                },
            )
            views.setContentDescription(
                R.id.widget_next,
                if (state.hasTrack) "Next $itemLabel" else "Next local sample track",
            )
            views.setContentDescription(
                R.id.widget_search,
                "Open search screen",
            )

            views.setOnClickPendingIntent(
                R.id.widget_previous,
                playbackCommandPendingIntent(context, ACTION_WIDGET_PREVIOUS, appWidgetId),
            )
            views.setOnClickPendingIntent(
                R.id.widget_play_pause,
                playbackCommandPendingIntent(
                    context = context,
                    action = ACTION_WIDGET_PLAY_PAUSE,
                    appWidgetId = appWidgetId,
                    shouldPlay = !state.isPlaying,
                ),
            )
            views.setOnClickPendingIntent(
                R.id.widget_next,
                playbackCommandPendingIntent(context, ACTION_WIDGET_NEXT, appWidgetId),
            )
            views.setOnClickPendingIntent(
                R.id.widget_search,
                openWidgetSearchPendingIntent(context, appWidgetId),
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun handlePlaybackCommand(context: Context, intent: Intent) {
            val state = MusicWidgetStateStore.current(context)
            val requestedShouldPlay = intent
                .takeIf { command -> command.hasExtra(MusicPlaybackService.EXTRA_WIDGET_SHOULD_PLAY) }
                ?.getBooleanExtra(MusicPlaybackService.EXTRA_WIDGET_SHOULD_PLAY, false)
            if (intent.action == ACTION_WIDGET_PLAY_PAUSE && requestedShouldPlay == false) {
                MusicWidgetStateStore.setPlaying(context, isPlaying = false)
                updateAllWidgets(context)
                if (!state.hasTrack) return
            }

            val serviceIntent = Intent(context, MusicPlaybackService::class.java)
                .setAction(intent.action)
                .putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID),
                )
            if (requestedShouldPlay != null) {
                serviceIntent.putExtra(MusicPlaybackService.EXTRA_WIDGET_SHOULD_PLAY, requestedShouldPlay)
            }

            startPlaybackService(context, serviceIntent)
        }

        private fun startPlaybackService(
            context: Context,
            intent: Intent,
        ) {
            try {
                context.startForegroundService(intent)
            } catch (_: RuntimeException) {
                MusicWidgetStateStore.setPlaying(context, isPlaying = false)
                updateAllWidgets(context)
            }
        }

        private fun playbackCommandPendingIntent(
            context: Context,
            action: String,
            appWidgetId: Int,
            shouldPlay: Boolean? = null,
        ): PendingIntent {
            val intent = Intent(context, MusicPlayerWidgetProvider::class.java)
                .setAction(action)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            if (shouldPlay != null) {
                intent.putExtra(MusicPlaybackService.EXTRA_WIDGET_SHOULD_PLAY, shouldPlay)
            }

            return PendingIntent.getBroadcast(
                context,
                action.hashCode() + appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private fun openWidgetSearchPendingIntent(
            context: Context,
            appWidgetId: Int,
        ): PendingIntent {
            val intent = Intent(context, WidgetMusicSearchActivity::class.java)
                .setAction(ACTION_WIDGET_OPEN_SEARCH)
                .putExtra(EXTRA_OPEN_SEARCH, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

            return PendingIntent.getActivity(
                context,
                ACTION_WIDGET_OPEN_SEARCH.hashCode() + appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}
