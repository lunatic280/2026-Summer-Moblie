package com.job.androidprojet.data.online

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CombinedOnlineMusicRepositoryTest {
    @Test
    fun search_whenOneProviderFails_returnsSuccessfulProviderResults() = runBlocking {
        val repository = CombinedOnlineMusicRepository(
            providers = listOf(
                FakeProvider(
                    source = OnlineMusicSource.ITunes,
                    result = Result.success(listOf(testResult(source = OnlineMusicSource.ITunes))),
                ),
                FakeProvider(
                    source = OnlineMusicSource.MusicBrainz,
                    result = Result.failure(IllegalStateException("MusicBrainz unavailable")),
                ),
            ),
        )

        val result = repository.search(" kpop ")

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(OnlineMusicSource.ITunes),
            result.getOrThrow().map { item -> item.source },
        )
    }

    @Test
    fun search_whenITunesFails_returnsMusicBrainzMetadataResults() = runBlocking {
        val repository = CombinedOnlineMusicRepository(
            providers = listOf(
                FakeProvider(
                    source = OnlineMusicSource.ITunes,
                    result = Result.failure(IllegalStateException("iTunes unavailable")),
                ),
                FakeProvider(
                    source = OnlineMusicSource.MusicBrainz,
                    result = Result.success(
                        listOf(
                            testResult(
                                source = OnlineMusicSource.MusicBrainz,
                                previewUrl = null,
                            ),
                        ),
                    ),
                ),
            ),
        )

        val result = repository.search("newjeans")

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(OnlineMusicSource.MusicBrainz),
            result.getOrThrow().map { item -> item.source },
        )
    }

    @Test
    fun search_whenAllProvidersFail_returnsFailure() = runBlocking {
        val repository = CombinedOnlineMusicRepository(
            providers = listOf(
                FakeProvider(
                    source = OnlineMusicSource.ITunes,
                    result = Result.failure(IllegalStateException("iTunes unavailable")),
                ),
                FakeProvider(
                    source = OnlineMusicSource.MusicBrainz,
                    result = Result.failure(IllegalStateException("MusicBrainz unavailable")),
                ),
            ),
        )

        val result = repository.search("kpop")

        assertTrue(result.isFailure)
    }

    @Test
    fun search_whenProviderSucceedsWithNoResultsAndAnotherFails_returnsEmptySuccess() = runBlocking {
        val repository = CombinedOnlineMusicRepository(
            providers = listOf(
                FakeProvider(
                    source = OnlineMusicSource.ITunes,
                    result = Result.success(emptyList()),
                ),
                FakeProvider(
                    source = OnlineMusicSource.MusicBrainz,
                    result = Result.failure(IllegalStateException("MusicBrainz unavailable")),
                ),
            ),
        )

        val result = repository.search("unknown track")

        assertTrue(result.isSuccess)
        assertEquals(emptyList<OnlineMusicResult>(), result.getOrThrow())
    }

    @Test
    fun search_deduplicatesBySourceTitleAndArtist() = runBlocking {
        val duplicate = testResult(source = OnlineMusicSource.ITunes)
        val repository = CombinedOnlineMusicRepository(
            providers = listOf(
                FakeProvider(
                    source = OnlineMusicSource.ITunes,
                    result = Result.success(listOf(duplicate, duplicate.copy(id = "duplicate"))),
                ),
            ),
        )

        val result = repository.search("kpop")

        assertEquals(1, result.getOrThrow().size)
    }

    private class FakeProvider(
        override val source: OnlineMusicSource,
        private val result: Result<List<OnlineMusicResult>>,
    ) : OnlineMusicSearchProvider {
        override suspend fun search(query: String): List<OnlineMusicResult> {
            return result.getOrThrow()
        }
    }

    private companion object {
        fun testResult(
            source: OnlineMusicSource,
            previewUrl: String? = "https://example.com/preview.m4a",
        ): OnlineMusicResult {
            return OnlineMusicResult(
                id = "${source.name}-1",
                title = "Hype Boy",
                artist = "NewJeans",
                album = "New Jeans",
                source = source,
                previewUrl = previewUrl,
            )
        }
    }
}
