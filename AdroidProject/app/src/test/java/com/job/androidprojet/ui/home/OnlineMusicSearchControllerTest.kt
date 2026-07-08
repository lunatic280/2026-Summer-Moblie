package com.job.androidprojet.ui.home

import com.job.androidprojet.data.online.OnlineMusicRepository
import com.job.androidprojet.data.online.OnlineMusicResult
import com.job.androidprojet.data.online.OnlineMusicSearchState
import com.job.androidprojet.data.online.OnlineMusicSource
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import org.junit.Assert.assertEquals
import org.junit.Test

class OnlineMusicSearchControllerTest {
    @Test
    fun search_whenRepositoryMissing_emitsIdle() {
        val harness = createHarness(repository = null)

        try {
            harness.controller.search("daft punk")

            assertEquals(listOf(OnlineMusicSearchState.Idle), harness.states)
        } finally {
            harness.cancel()
        }
    }

    @Test
    fun search_whenQueryIsTooShort_emitsIdleAndDoesNotSearchRepository() {
        val repository = FakeOnlineMusicRepository { Result.success(testResults) }
        val harness = createHarness(repository)

        try {
            harness.controller.search(" p ")

            assertEquals(listOf(OnlineMusicSearchState.Idle), harness.states)
            assertEquals(emptyList<String>(), repository.queries)
        } finally {
            harness.cancel()
        }
    }

    @Test
    fun search_whenRepositorySucceeds_emitsLoadingThenSuccessWithTrimmedQuery() {
        val repository = FakeOnlineMusicRepository { query ->
            Result.success(testResults.map { result -> result.copy(title = query) })
        }
        val harness = createHarness(repository)

        try {
            harness.controller.search("  city pop  ")

            val success = harness.states.awaitLastState<OnlineMusicSearchState.Success>()

            assertEquals(listOf("city pop"), repository.queries)
            assertEquals(
                listOf(
                    OnlineMusicSearchState.Loading,
                    success,
                ),
                harness.states,
            )
            assertEquals(listOf("city pop"), success.results.map { result -> result.title })
        } finally {
            harness.cancel()
        }
    }

    @Test
    fun search_whenRepositoryFails_emitsLoadingThenError() {
        val repository = FakeOnlineMusicRepository {
            Result.failure(IllegalStateException("network unavailable"))
        }
        val harness = createHarness(repository)

        try {
            harness.controller.search("preview")

            val error = harness.states.awaitLastState<OnlineMusicSearchState.Error>()

            assertEquals(listOf("preview"), repository.queries)
            assertEquals(
                listOf(
                    OnlineMusicSearchState.Loading,
                    error,
                ),
                harness.states,
            )
            assertEquals(
                "Online API preview search failed. Check the network, then try again. Local sample tracks still work offline.",
                error.message,
            )
        } finally {
            harness.cancel()
        }
    }

    @Test
    fun search_whenShortQueryReplacesPendingSearch_cancelsSearchAndEmitsIdle() {
        val repository = FakeOnlineMusicRepository { Result.success(testResults) }
        val harness = createHarness(repository)

        try {
            harness.controller.search("preview")
            harness.controller.search("p")

            waitPastDebounce()

            assertEquals(
                listOf(
                    OnlineMusicSearchState.Loading,
                    OnlineMusicSearchState.Idle,
                ),
                harness.states,
            )
            assertEquals(emptyList<String>(), repository.queries)
        } finally {
            harness.cancel()
        }
    }

    @Test
    fun search_whenNewQueryReplacesPendingSearch_onlyPublishesLatestResult() {
        val repository = FakeOnlineMusicRepository { query ->
            Result.success(testResults.map { result -> result.copy(title = query) })
        }
        val harness = createHarness(repository)

        try {
            harness.controller.search("first")
            harness.controller.search("second")

            val success = harness.states.awaitLastState<OnlineMusicSearchState.Success>()

            assertEquals(listOf("second"), repository.queries)
            assertEquals(
                listOf(
                    OnlineMusicSearchState.Loading,
                    OnlineMusicSearchState.Loading,
                    success,
                ),
                harness.states,
            )
            assertEquals(listOf("second"), success.results.map { result -> result.title })
        } finally {
            harness.cancel()
        }
    }

    private fun createHarness(repository: OnlineMusicRepository?): ControllerHarness {
        val states = CopyOnWriteArrayList<OnlineMusicSearchState>()
        val job = SupervisorJob()
        val scope = CoroutineScope(job + Dispatchers.Default)
        val controller = OnlineMusicSearchController(
            repository = repository,
            scope = scope,
            onStateChanged = { state -> states += state },
        )
        return ControllerHarness(controller, states, job)
    }

    private inline fun <reified T : OnlineMusicSearchState> CopyOnWriteArrayList<OnlineMusicSearchState>.awaitLastState(
        timeoutMillis: Long = 2_500L,
    ): T {
        val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis)
        while (System.nanoTime() < deadline) {
            val lastState = lastOrNull()
            if (lastState is T) {
                return lastState
            }
            Thread.sleep(10L)
        }
        throw AssertionError("Timed out waiting for ${T::class.java.simpleName}. States: $this")
    }

    private fun waitPastDebounce() {
        Thread.sleep(1_250L)
    }

    private class ControllerHarness(
        val controller: OnlineMusicSearchController,
        val states: CopyOnWriteArrayList<OnlineMusicSearchState>,
        private val job: Job,
    ) {
        fun cancel() {
            job.cancel()
        }
    }

    private class FakeOnlineMusicRepository(
        private val resultForQuery: (String) -> Result<List<OnlineMusicResult>>,
    ) : OnlineMusicRepository {
        val queries = CopyOnWriteArrayList<String>()

        override suspend fun search(query: String): Result<List<OnlineMusicResult>> {
            queries += query
            return resultForQuery(query)
        }
    }

    private companion object {
        val testResults = listOf(
            OnlineMusicResult(
                id = "preview-1",
                title = "Preview One",
                artist = "API Artist",
                album = "API Album",
                source = OnlineMusicSource.ITunes,
                previewUrl = "https://example.com/preview-one.m4a",
            ),
        )
    }
}
