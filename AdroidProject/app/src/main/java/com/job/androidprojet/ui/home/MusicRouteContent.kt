package com.job.androidprojet.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal fun MusicRouteContent(
    trackCount: Int,
    searchQuery: String,
    selectedFilter: MusicContentFilter,
    resultCount: Int,
    onQueryChange: (String) -> Unit,
    onFilterSelected: (MusicContentFilter) -> Unit,
    modifier: Modifier = Modifier,
    canPinWidget: Boolean = false,
    onPinWidgetClick: (() -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 24.dp,
            end = 16.dp,
            bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HomeHeader(
                trackCount = trackCount,
                canPinWidget = canPinWidget && onPinWidgetClick != null,
                onPinWidgetClick = onPinWidgetClick,
            )
        }

        item {
            LocalSearchField(
                query = searchQuery,
                onQueryChange = onQueryChange,
            )
        }

        item {
            ContentFilterRow(
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
            )
        }

        item {
            SearchSummary(
                query = searchQuery,
                filter = selectedFilter,
                resultCount = resultCount,
            )
        }

        content()
    }
}

@Composable
private fun HomeHeader(
    trackCount: Int,
    modifier: Modifier = Modifier,
    canPinWidget: Boolean = false,
    onPinWidgetClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(HomeAccent, HomeAccentWarm),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "LM",
                color = Color(0xFF08100B),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                maxLines = 1,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = "Local Mix",
                color = HomeTextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$trackCount local sample tracks",
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (canPinWidget && onPinWidgetClick != null) {
            Button(
                onClick = onPinWidgetClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HomeSurfaceVariant,
                    contentColor = HomeTextPrimary,
                ),
            ) {
                Text(
                    text = "Add widget",
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun LocalSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        label = {
            Text(text = "Search local music")
        },
        placeholder = {
            Text(text = "Title, artist, or album")
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = HomeTextPrimary,
            unfocusedTextColor = HomeTextPrimary,
            focusedContainerColor = HomeSurface,
            unfocusedContainerColor = HomeSurface,
            focusedBorderColor = HomeAccent,
            unfocusedBorderColor = HomeSurfaceVariant,
            cursorColor = HomeAccent,
            focusedLabelColor = HomeTextSecondary,
            unfocusedLabelColor = HomeTextMuted,
            focusedPlaceholderColor = HomeTextMuted,
            unfocusedPlaceholderColor = HomeTextMuted,
        ),
    )
}

@Composable
private fun ContentFilterRow(
    selectedFilter: MusicContentFilter,
    onFilterSelected: (MusicContentFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MusicContentFilter.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = HomeSurface,
                    labelColor = HomeTextSecondary,
                    selectedContainerColor = HomeAccent,
                    selectedLabelColor = Color(0xFF07100B),
                ),
            )
        }
    }
}

@Composable
private fun SearchSummary(
    query: String,
    filter: MusicContentFilter,
    resultCount: Int,
    modifier: Modifier = Modifier,
) {
    val resultLabel = when (filter) {
        MusicContentFilter.Albums -> if (resultCount == 1) "album" else "albums"
        else -> if (resultCount == 1) "track" else "tracks"
    }
    val summary = if (query.isBlank()) {
        "$resultCount $resultLabel"
    } else {
        "$resultCount $resultLabel for \"$query\""
    }

    Text(
        text = summary,
        modifier = modifier.fillMaxWidth(),
        color = HomeTextSecondary,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
