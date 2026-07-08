package com.job.androidprojet.data.online

import kotlinx.coroutines.CancellationException

interface OnlineMusicRepository {
    suspend fun search(query: String): Result<List<OnlineMusicResult>>
}

interface OnlineMusicSearchProvider {
    val source: OnlineMusicSource
    suspend fun search(query: String): List<OnlineMusicResult>
}

class CombinedOnlineMusicRepository(
    private val providers: List<OnlineMusicSearchProvider> = listOf(
        ITunesSearchClient(),
        MusicBrainzClient(),
    ),
) : OnlineMusicRepository {
    override suspend fun search(query: String): Result<List<OnlineMusicResult>> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.length < MIN_QUERY_LENGTH) {
            return Result.success(emptyList())
        }

        val providerResults = providers.map { provider ->
            searchProvider(provider, normalizedQuery)
        }
        val successfulResults = providerResults
            .mapNotNull { result -> result.getOrNull() }
            .flatten()

        if (successfulResults.isNotEmpty() || providerResults.any { result -> result.isSuccess }) {
            return Result.success(
                successfulResults
                    .distinctBy { result ->
                        "${result.source}-${result.title.lowercase()}-${result.artist.lowercase()}"
                    }
                    .take(MAX_RESULTS),
            )
        }

        return Result.failure(
            providerResults.firstNotNullOfOrNull { result -> result.exceptionOrNull() }
                ?: IllegalStateException("No online music providers are configured."),
        )
    }

    private companion object {
        const val MIN_QUERY_LENGTH = 2
        const val MAX_RESULTS = 12
    }
}

private suspend fun searchProvider(
    provider: OnlineMusicSearchProvider,
    query: String,
): Result<List<OnlineMusicResult>> {
    return try {
        Result.success(provider.search(query))
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        Result.failure(error)
    }
}
