package com.job.androidprojet.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.job.androidprojet.model.Music

@Composable
internal fun PlayerDetailScreen(
    music: Music?,
    isPlaying: Boolean,
    positionMillis: Long,
    progress: Float,
    queuePreview: List<Music>,
    onProgressChange: (Float) -> Unit,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleFavorite: () -> Unit,
    onQueueTrackClick: (Music) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (music == null) {
        EmptyMusicList(
            message = "No local tracks available",
            modifier = modifier,
        )
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PlayerHeader(music = music)
        PlayerAlbumArt(music = music)
        PlayerMetadata(
            music = music,
            onToggleFavorite = onToggleFavorite,
        )
        PlayerTimeline(
            positionMillis = positionMillis,
            durationMillis = music.durationMillis,
            progress = progress,
            onProgressChange = onProgressChange,
        )
        PlayerControls(
            isPlaying = isPlaying,
            onPrevious = onPrevious,
            onTogglePlay = onTogglePlay,
            onNext = onNext,
        )
        QueuePreview(
            music = queuePreview,
            onMusicClick = onQueueTrackClick,
        )
    }
}

@Composable
private fun PlayerHeader(
    music: Music,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Now playing",
            color = HomeTextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = music.album,
            color = HomeTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun PlayerAlbumArt(
    music: Music,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        AlbumPlaceholder(
            music = music,
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .widthIn(max = 340.dp)
                .aspectRatio(1f),
            cornerRadius = 8.dp,
        )
    }
}

@Composable
private fun PlayerMetadata(
    music: Music,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = music.title,
                color = HomeTextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = music.artist,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        FavoriteActionButton(
            isFavorite = music.isFavorite,
            onClick = onToggleFavorite,
        )
    }
}

@Composable
private fun FavoriteActionButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = if (isFavorite) "Saved" else "Save"
    val containerColor = if (isFavorite) HomeAccent else HomeSurfaceVariant
    val contentColor = if (isFavorite) Color(0xFF06100A) else HomeTextPrimary

    Box(
        modifier = modifier
            .height(44.dp)
            .widthIn(min = 72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .clickable(
                role = Role.Button,
                onClickLabel = label,
                onClick = onClick,
            )
            .semantics {
                contentDescription = if (isFavorite) {
                    "Remove ${label.lowercase()} mark"
                } else {
                    "Save local track"
                }
            }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun PlayerTimeline(
    positionMillis: Long,
    durationMillis: Long,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Slider(
            value = progress.coerceIn(0f, 1f),
            onValueChange = onProgressChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Playback position"
                },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = HomeAccent,
                activeTrackColor = HomeAccent,
                inactiveTrackColor = HomeSurfaceVariant,
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = formatDuration(positionMillis),
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
            Text(
                text = formatDuration(durationMillis),
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun PlayerControls(
    isPlaying: Boolean,
    onPrevious: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerControlButton(
            label = "<<",
            contentDescription = "Previous local track",
            onClick = onPrevious,
        )
        PlayerControlButton(
            label = if (isPlaying) "II" else ">",
            contentDescription = if (isPlaying) {
                "Pause local track"
            } else {
                "Play local track"
            },
            emphasized = true,
            onClick = onTogglePlay,
        )
        PlayerControlButton(
            label = ">>",
            contentDescription = "Next local track",
            onClick = onNext,
        )
    }
}

@Composable
private fun PlayerControlButton(
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    val size = if (emphasized) 76.dp else 60.dp
    val containerColor = if (emphasized) HomeAccent else HomeSurfaceVariant
    val contentColor = if (emphasized) Color(0xFF06100A) else HomeTextPrimary
    val textStyle = if (emphasized) {
        MaterialTheme.typography.headlineSmall
    } else {
        MaterialTheme.typography.titleMedium
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = onClick,
            )
            .semantics {
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = contentColor,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
private fun QueuePreview(
    music: List<Music>,
    onMusicClick: (Music) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionTitle(
            title = "Up next",
            subtitle = "From the sample catalog",
        )
        if (music.isEmpty()) {
            EmptyMusicList(message = "No more local tracks")
        } else {
            music.forEach { track ->
                MusicListRow(
                    music = track,
                    onClick = { onMusicClick(track) },
                )
            }
        }
    }
}
