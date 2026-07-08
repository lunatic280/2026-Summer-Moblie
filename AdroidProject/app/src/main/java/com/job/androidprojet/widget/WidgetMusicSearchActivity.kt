package com.job.androidprojet.widget

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.job.androidprojet.data.SampleMusicCatalog
import com.job.androidprojet.model.Music
import com.job.androidprojet.playback.MusicPlaybackService
import com.job.androidprojet.ui.theme.AndroidProjetTheme

class WidgetMusicSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var errorMessage by remember { mutableStateOf<String?>(null) }

            AndroidProjetTheme(
                darkTheme = true,
                dynamicColor = false,
            ) {
                WidgetMusicSearchScreen(
                    songs = SampleMusicCatalog.songs,
                    errorMessage = errorMessage,
                    onTrackSelected = { music ->
                        if (playTrack(music.id)) {
                            finish()
                        } else {
                            errorMessage = "Could not start playback from the widget."
                        }
                    },
                    onClose = ::finish,
                )
            }
        }
    }

    private fun playTrack(musicId: Long): Boolean {
        val intent = Intent(this, MusicPlaybackService::class.java)
            .setAction(MusicPlaybackService.ACTION_WIDGET_PLAY_TRACK)
            .putExtra(MusicPlaybackService.EXTRA_WIDGET_MUSIC_ID, musicId)

        return try {
            startForegroundService(intent)
            true
        } catch (_: RuntimeException) {
            MusicWidgetStateStore.setPlaying(this, isPlaying = false)
            MusicPlayerWidgetProvider.updateAllWidgets(this)
            false
        }
    }
}

@Composable
private fun WidgetMusicSearchScreen(
    songs: List<Music>,
    errorMessage: String?,
    onTrackSelected: (Music) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredSongs = remember(query, songs) {
        songs.filterByQuery(query)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Widget search",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Choose a local sample track",
                    color = Color(0xFFB3B3B3),
                    fontSize = 13.sp,
                )
            }
            TextButton(onClick = onClose) {
                Text(text = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { value -> query = value },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text(text = "Search songs")
            },
            placeholder = {
                Text(text = "Title, artist, or album")
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color(0xFFFFB4AB),
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = "${filteredSongs.size} local songs",
            color = Color(0xFFB3B3B3),
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredSongs.isEmpty()) {
            EmptySearchResult()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = filteredSongs,
                    key = { music -> music.id },
                ) { music ->
                    WidgetSearchResultRow(
                        music = music,
                        onClick = { onTrackSelected(music) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetSearchResultRow(
    music: Music,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1F1F1F))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = music.title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${music.artist} - ${music.album}",
                color = Color(0xFFB3B3B3),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1DB954),
                contentColor = Color.Black,
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(text = "Play")
        }
    }
}

@Composable
private fun EmptySearchResult(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1F1F1F))
            .padding(16.dp),
    ) {
        Text(
            text = "No local songs found",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Try another title, artist, or album.",
            color = Color(0xFFB3B3B3),
            fontSize = 12.sp,
        )
    }
}

private fun List<Music>.filterByQuery(query: String): List<Music> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) return this

    return filter { music ->
        music.title.contains(normalizedQuery, ignoreCase = true) ||
            music.artist.contains(normalizedQuery, ignoreCase = true) ||
            music.album.contains(normalizedQuery, ignoreCase = true)
    }
}
