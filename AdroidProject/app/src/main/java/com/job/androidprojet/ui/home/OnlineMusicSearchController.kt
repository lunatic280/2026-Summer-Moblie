package com.job.androidprojet.ui.home

import com.job.androidprojet.data.online.OnlineMusicRepository
import com.job.androidprojet.data.online.OnlineMusicSearchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class OnlineMusicSearchController(
    private val repository: OnlineMusicRepository?,
    private val scope: CoroutineScope,
    private val onStateChanged: (OnlineMusicSearchState) -> Unit,
) {
    private var searchJob: Job? = null

    fun search(query: String) {
        val normalizedQuery = query.trim()
        searchJob?.cancel()

        if (repository == null || normalizedQuery.length < MIN_QUERY_LENGTH) {
            onStateChanged(OnlineMusicSearchState.Idle)
            return
        }

        onStateChanged(OnlineMusicSearchState.Loading)
        searchJob = scope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            val nextState = repository.search(normalizedQuery).fold(
                onSuccess = { results -> OnlineMusicSearchState.Success(results) },
                onFailure = {
                    OnlineMusicSearchState.Error(
                        message = "Online API preview search failed. Check the network, then try again. Local sample tracks still work offline.",
                    )
                },
            )
            onStateChanged(nextState)
        }
    }

    private companion object {
        const val MIN_QUERY_LENGTH = 2
        const val SEARCH_DEBOUNCE_MILLIS = 1_000L
    }
}
