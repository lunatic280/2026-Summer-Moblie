package com.job.androidprojet.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.job.androidprojet.model.Music

@Composable
internal fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(
            text = title,
            color = HomeTextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = HomeTextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun QuickAccessGrid(
    music: List<Music>,
    onMusicClick: (Music) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        music.chunked(2).forEach { rowTracks ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowTracks.forEach { track ->
                    QuickAccessTile(
                        music = track,
                        modifier = Modifier.weight(1f),
                        onClick = { onMusicClick(track) },
                    )
                }
                if (rowTracks.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickAccessTile(
    music: Music,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val tileDescription = "Play ${music.title} by ${music.artist}"

    Card(
        modifier = modifier
            .height(64.dp)
            .clickable(
                role = Role.Button,
                onClickLabel = tileDescription,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = tileDescription
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = HomeSurfaceElevated),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AlbumPlaceholder(
                music = music,
                modifier = Modifier.size(64.dp),
                cornerRadius = 8.dp,
            )
            Text(
                text = music.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                color = HomeTextPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun HorizontalMusicSection(
    title: String,
    music: List<Music>,
    onMusicClick: (Music) -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String = "No tracks yet",
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionTitle(title = title)
        if (music.isEmpty()) {
            EmptyMusicList(message = emptyMessage)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = music,
                    key = { track -> "$title-${track.id}" },
                ) { track ->
                    RecommendationCard(
                        music = track,
                        onClick = { onMusicClick(track) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    music: Music,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val cardDescription = "Play ${music.title} by ${music.artist}"

    Column(
        modifier = modifier
            .width(148.dp)
            .clickable(
                role = Role.Button,
                onClickLabel = cardDescription,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = cardDescription
            },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AlbumPlaceholder(
            music = music,
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp),
        )
        Text(
            text = music.title,
            color = HomeTextPrimary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = music.artist,
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun MusicListRow(
    music: Music,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val durationText = formatDuration(music.durationMillis)
    val favoriteDescription = if (music.isFavorite) ", favorite" else ""
    val rowDescription = "${music.title}, ${music.artist}, ${music.album}, " +
        "$durationText$favoriteDescription"
    val rowModifier = modifier
        .fillMaxWidth()
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    role = Role.Button,
                    onClickLabel = "Select ${music.title}",
                    onClick = onClick,
                )
            } else {
                Modifier
            },
        )
        .semantics(mergeDescendants = true) {
            contentDescription = rowDescription
        }

    Card(
        modifier = rowModifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = HomeSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AlbumPlaceholder(
                music = music,
                modifier = Modifier.size(56.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = music.title,
                        modifier = Modifier.weight(1f),
                        color = HomeTextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (music.isFavorite) {
                        FavoritePill()
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${music.artist} - ${music.album}",
                    color = HomeTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = durationText,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun FavoritePill(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(HomeAccent.copy(alpha = 0.16f))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Fav",
            color = HomeAccent,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
internal fun LibraryStatsRow(
    allMusic: List<Music>,
    modifier: Modifier = Modifier,
) {
    val favoriteCount = allMusic.count { track -> track.isFavorite }
    val albumCount = allMusic.distinctBy { track -> track.album }.size

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        LibraryStatCard(
            label = "Tracks",
            value = allMusic.size.toString(),
            modifier = Modifier.weight(1f),
        )
        LibraryStatCard(
            label = "Albums",
            value = albumCount.toString(),
            modifier = Modifier.weight(1f),
        )
        LibraryStatCard(
            label = "Favs",
            value = favoriteCount.toString(),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LibraryStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.heightIn(min = 74.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = HomeSurfaceElevated),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = value,
                color = HomeTextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            Text(
                text = label,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun EmptyMusicList(
    message: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = HomeSurface),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
internal fun AlbumPlaceholder(
    music: Music,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        albumAccentColor(music.id),
                        HomeSurfaceVariant,
                    ),
                ),
            )
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = music.albumInitials(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

private fun albumAccentColor(id: Long): Color {
    val colors = listOf(
        HomeAccent,
        Color(0xFF5AA9E6),
        Color(0xFFE0A458),
        Color(0xFFE85D75),
        Color(0xFF8BD450),
        Color(0xFFB68CFF),
        Color(0xFF5ED1C6),
        HomeAccentWarm,
    )
    val index = ((id - 1) % colors.size).toInt().coerceAtLeast(0)
    return colors[index]
}

private fun Music.albumInitials(): String {
    val initials = title
        .split(" ")
        .mapNotNull { word -> word.firstOrNull()?.uppercaseChar()?.toString() }
        .take(2)
        .joinToString(separator = "")

    return initials.ifBlank { "M" }
}

internal fun formatDuration(durationMillis: Long): String {
    val totalSeconds = (durationMillis / 1_000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
