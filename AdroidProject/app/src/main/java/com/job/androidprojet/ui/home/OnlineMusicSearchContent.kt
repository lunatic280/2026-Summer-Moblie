package com.job.androidprojet.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.job.androidprojet.data.online.OnlineMusicResult
import com.job.androidprojet.data.online.OnlineMusicSearchState

internal fun LazyListScope.homePreviewContent(
    homePreviewState: OnlineMusicSearchState,
    previewMusicId: String?,
    previewIsPlaying: Boolean,
    onPreviewToggle: ((OnlineMusicResult, List<OnlineMusicResult>) -> Unit)?,
    onRetry: (() -> Unit)?,
) {
    item {
        SectionTitle(
            title = "API preview clips",
            subtitle = "30-second preview URLs only, separate from local sample tracks",
        )
    }

    when (homePreviewState) {
        OnlineMusicSearchState.Idle -> item {
            OnlineStatusCard(message = "API preview recommendations are not configured")
        }

        OnlineMusicSearchState.Loading -> item {
            OnlineStatusCard(
                message = "Loading API preview clips...",
                isLoading = true,
            )
        }

        is OnlineMusicSearchState.Error -> item {
            OnlineStatusCard(
                message = homePreviewState.message,
                actionLabel = "Retry previews",
                onAction = onRetry,
            )
        }

        is OnlineMusicSearchState.Success -> item {
            HomePreviewRow(
                results = homePreviewState.results,
                previewMusicId = previewMusicId,
                previewIsPlaying = previewIsPlaying,
                onPreviewToggle = onPreviewToggle,
            )
        }
    }
}

@Composable
private fun HomePreviewRow(
    results: List<OnlineMusicResult>,
    previewMusicId: String?,
    previewIsPlaying: Boolean,
    onPreviewToggle: ((OnlineMusicResult, List<OnlineMusicResult>) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val previewRecommendations = results.filter { result ->
        result.previewUrl.isValidPreviewUrl()
    }

    if (previewRecommendations.isEmpty()) {
        EmptyMusicList(
            message = "No playable API preview clips found",
            modifier = modifier,
        )
        return
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = previewRecommendations,
            key = { result -> "home-preview-${result.id}" },
        ) { result ->
            HomePreviewCard(
                result = result,
                isPreviewPlaying = previewMusicId == result.id && previewIsPlaying,
                previewRecommendations = previewRecommendations,
                onPreviewToggle = onPreviewToggle,
            )
        }
    }
}

@Composable
private fun HomePreviewCard(
    result: OnlineMusicResult,
    isPreviewPlaying: Boolean,
    previewRecommendations: List<OnlineMusicResult>,
    onPreviewToggle: ((OnlineMusicResult, List<OnlineMusicResult>) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(164.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = result.accessibilityDescription()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = HomeSurface),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OnlineArtwork(
                result = result,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp),
            )
            Text(
                text = result.title,
                color = HomeTextPrimary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = result.artist,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "API preview clip, 30s max",
                color = HomeTextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (onPreviewToggle != null) {
                Button(
                    onClick = { onPreviewToggle(result, previewRecommendations) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.semantics {
                        onClick(
                            label = if (isPreviewPlaying) {
                                "Stop preview for ${result.title}"
                            } else {
                                "Play 30-second API preview for ${result.title}"
                            },
                            action = null,
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPreviewPlaying) {
                            HomeSurfaceVariant
                        } else {
                            HomeAccent
                        },
                        contentColor = if (isPreviewPlaying) {
                            HomeTextPrimary
                        } else {
                            HomeBackground
                        },
                    ),
                ) {
                    Text(
                        text = if (isPreviewPlaying) "Stop 30s" else "Play 30s",
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

internal fun LazyListScope.onlineSearchContent(
    query: String,
    onlineSearchState: OnlineMusicSearchState,
    previewMusicId: String?,
    previewIsPlaying: Boolean,
    onPreviewToggle: ((OnlineMusicResult, List<OnlineMusicResult>) -> Unit)?,
    onRetry: (() -> Unit)?,
) {
    if (query.trim().length < 2) return

    item {
        SectionTitle(
            title = "Online API results",
            subtitle = "Metadata plus 30-second preview clips when available; not Spotify or downloads",
        )
    }

    when (onlineSearchState) {
        OnlineMusicSearchState.Idle -> item {
            OnlineStatusCard(message = "Online API preview search is not configured")
        }

        OnlineMusicSearchState.Loading -> item {
            OnlineStatusCard(
                message = "Searching online API preview metadata...",
                isLoading = true,
            )
        }

        is OnlineMusicSearchState.Error -> item {
            OnlineStatusCard(
                message = onlineSearchState.message,
                actionLabel = "Retry search",
                onAction = onRetry,
            )
        }

        is OnlineMusicSearchState.Success -> onlineSearchResults(
            results = onlineSearchState.results,
            previewMusicId = previewMusicId,
            previewIsPlaying = previewIsPlaying,
            onPreviewToggle = onPreviewToggle,
        )
    }
}

private fun LazyListScope.onlineSearchResults(
    results: List<OnlineMusicResult>,
    previewMusicId: String?,
    previewIsPlaying: Boolean,
    onPreviewToggle: ((OnlineMusicResult, List<OnlineMusicResult>) -> Unit)?,
) {
    if (results.isEmpty()) {
        item {
            EmptyMusicList(message = "No online API metadata results found")
        }
        return
    }

    val previewRecommendations = results.filter { result ->
        result.previewUrl.isValidPreviewUrl()
    }

    items(
        items = results,
        key = { result -> "online-${result.id}" },
    ) { result ->
        OnlineMusicResultRow(
            result = result,
            isPreviewPlaying = previewMusicId == result.id && previewIsPlaying,
            previewRecommendations = previewRecommendations,
            onPreviewToggle = onPreviewToggle,
        )
    }
}

@Composable
private fun OnlineStatusCard(
    message: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = HomeSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = HomeAccent,
                    strokeWidth = 2.dp,
                )
            }
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (actionLabel != null && onAction != null) {
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HomeSurfaceVariant,
                        contentColor = HomeTextPrimary,
                    ),
                ) {
                    Text(
                        text = actionLabel,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun OnlineMusicResultRow(
    result: OnlineMusicResult,
    modifier: Modifier = Modifier,
    isPreviewPlaying: Boolean = false,
    previewRecommendations: List<OnlineMusicResult> = emptyList(),
    onPreviewToggle: ((OnlineMusicResult, List<OnlineMusicResult>) -> Unit)? = null,
) {
    val canPlayPreview = result.previewUrl.isValidPreviewUrl()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = result.accessibilityDescription()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = HomeSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(HomeSurfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                OnlineArtwork(
                    result = result,
                    modifier = Modifier.size(56.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = result.title,
                    color = HomeTextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${result.artist} - ${result.albumLabel()}",
                    color = HomeTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${result.source.label} - ${result.previewLabel()}${result.releaseSuffix()}",
                    color = HomeTextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (canPlayPreview && onPreviewToggle != null) {
                    Button(
                        onClick = { onPreviewToggle(result, previewRecommendations) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.semantics {
                            onClick(
                                label = if (isPreviewPlaying) {
                                    "Stop preview for ${result.title}"
                                } else {
                                    "Play 30-second API preview for ${result.title}"
                                },
                                action = null,
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPreviewPlaying) {
                                HomeSurfaceVariant
                            } else {
                                HomeAccent
                            },
                            contentColor = if (isPreviewPlaying) {
                                HomeTextPrimary
                            } else {
                                HomeBackground
                            },
                        ),
                    ) {
                        Text(
                            text = if (isPreviewPlaying) "Stop preview" else "Play 30s preview",
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

private fun OnlineMusicResult.albumLabel(): String {
    return album ?: "Unknown album"
}

private fun OnlineMusicResult.previewLabel(): String {
    return if (previewUrl.isValidPreviewUrl()) {
        "30-second API preview clip available"
    } else {
        "Metadata only, no playable preview clip"
    }
}

private fun OnlineMusicResult.releaseSuffix(): String {
    return releaseDate?.let { date -> " - $date" }.orEmpty()
}

private fun OnlineMusicResult.accessibilityDescription(): String {
    return "$title, $artist, ${source.label}, ${previewLabel()}"
}

@Composable
private fun OnlineArtwork(
    result: OnlineMusicResult,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(HomeSurfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (result.artworkUrl != null) {
            AsyncImage(
                model = result.artworkUrl,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
            )
        } else {
            Text(
                text = result.source.label.take(2).uppercase(),
                color = HomeTextPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

private fun String?.isValidPreviewUrl(): Boolean {
    return this?.startsWith(prefix = "https://", ignoreCase = true) == true
}
