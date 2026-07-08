package com.job.androidprojet.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.job.androidprojet.data.online.OnlineMusicResult
import com.job.androidprojet.model.Music

@Composable
internal fun HomeBottomBar(
    currentMusic: Music?,
    isPlaying: Boolean,
    currentPreview: OnlineMusicResult?,
    isPreviewPlaying: Boolean,
    selectedDestination: HomeDestination,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onOpenPlayer: () -> Unit,
    onDestinationSelected: (HomeDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HomeBackground),
    ) {
        if (currentPreview != null && isPreviewPlaying) {
            PreviewMiniPlayerBar(preview = currentPreview)
        } else {
            if (currentMusic != null) {
                MiniPlayerBar(
                    music = currentMusic,
                    isPlaying = isPlaying,
                    onTogglePlay = onTogglePlay,
                    onPrevious = onPrevious,
                    onNext = onNext,
                    onOpenPlayer = onOpenPlayer,
                )
            }
        }

        NavigationBar(
            containerColor = HomeSurface,
            tonalElevation = 0.dp,
        ) {
            HomeDestination.entries.forEach { destination ->
                NavigationBarItem(
                    selected = selectedDestination == destination,
                    onClick = { onDestinationSelected(destination) },
                    icon = {
                        Text(
                            text = destination.indicator,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                    },
                    label = {
                        Text(
                            text = destination.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF06100A),
                        selectedTextColor = HomeTextPrimary,
                        indicatorColor = HomeAccent,
                        unselectedIconColor = HomeTextMuted,
                        unselectedTextColor = HomeTextMuted,
                    ),
                )
            }
        }
    }
}

@Composable
private fun PreviewMiniPlayerBar(
    preview: OnlineMusicResult,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = HomeSurfaceElevated,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = "Playing preview, ${preview.title} by ${preview.artist}"
                },
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(HomeSurfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (preview.artworkUrl != null) {
                    AsyncImage(
                        model = preview.artworkUrl,
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                    )
                } else {
                    Text(
                        text = "PR",
                        color = HomeTextPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = preview.title,
                    color = HomeTextPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${preview.artist} - 30s preview",
                    color = HomeTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(HomeAccent),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "ON",
                    color = Color(0xFF06100A),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
internal fun MiniPlayerBar(
    music: Music,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onOpenPlayer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemLabel = if (music.isOnlinePreview) {
        "API preview clip"
    } else {
        "local sample track"
    }
    val subtitle = if (music.isOnlinePreview) {
        "${music.artist} - 30s API preview"
    } else {
        music.artist
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = HomeSurfaceElevated,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        role = Role.Button,
                        onClickLabel = "Open player for ${music.title}",
                        onClick = onOpenPlayer,
                    )
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Open player, ${music.title} by ${music.artist}"
                    },
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MusicArtwork(
                    music = music,
                    modifier = Modifier.size(44.dp),
                    cornerRadius = 7.dp,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = music.title,
                        color = HomeTextPrimary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        color = HomeTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MiniControl(
                    label = "<<",
                    contentDescription = "Previous $itemLabel",
                    onClick = onPrevious,
                )
                MiniControl(
                    label = if (isPlaying) "II" else ">",
                    contentDescription = if (isPlaying) {
                        "Pause $itemLabel"
                    } else {
                        "Play $itemLabel"
                    },
                    emphasized = true,
                    onClick = onTogglePlay,
                )
                MiniControl(
                    label = ">>",
                    contentDescription = "Next $itemLabel",
                    onClick = onNext,
                )
            }
        }
    }
}

@Composable
private fun MiniControl(
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    val containerColor = if (emphasized) HomeAccent else HomeSurfaceVariant
    val contentColor = if (emphasized) Color(0xFF06100A) else HomeTextPrimary

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
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
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(containerColor)
                .clearAndSetSemantics {},
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}
