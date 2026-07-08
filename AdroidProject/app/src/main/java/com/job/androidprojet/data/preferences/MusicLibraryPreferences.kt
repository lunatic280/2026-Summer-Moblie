package com.job.androidprojet.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

data class PersistedMusicLibraryState(
    val favoriteMusicIds: Set<Long>? = null,
    val recentMusicIds: List<Long> = emptyList(),
)

interface MusicLibraryPreferences {
    val state: Flow<PersistedMusicLibraryState>

    suspend fun saveFavoriteMusicIds(ids: Set<Long>)

    suspend fun saveRecentMusicIds(ids: List<Long>)
}

class DataStoreMusicLibraryPreferences(
    context: Context,
) : MusicLibraryPreferences {
    private val dataStore = context.applicationContext.musicLibraryDataStore

    override val state: Flow<PersistedMusicLibraryState> = dataStore.data
        .catch {
            emit(androidx.datastore.preferences.core.emptyPreferences())
        }
        .map { preferences ->
            PersistedMusicLibraryState(
                favoriteMusicIds = preferences[FAVORITE_MUSIC_IDS]?.mapNotNull { id ->
                    id.toLongOrNull()
                }?.toSet(),
                recentMusicIds = preferences[RECENT_MUSIC_IDS]
                    ?.split(ID_SEPARATOR)
                    ?.mapNotNull { id -> id.toLongOrNull() }
                    .orEmpty(),
            )
        }

    override suspend fun saveFavoriteMusicIds(ids: Set<Long>) {
        dataStore.edit { preferences ->
            preferences[FAVORITE_MUSIC_IDS] = ids.map { id -> id.toString() }.toSet()
        }
    }

    override suspend fun saveRecentMusicIds(ids: List<Long>) {
        dataStore.edit { preferences ->
            preferences[RECENT_MUSIC_IDS] = ids.joinToString(separator = ID_SEPARATOR)
        }
    }

    private companion object {
        const val ID_SEPARATOR = ","
        val FAVORITE_MUSIC_IDS = stringSetPreferencesKey("favorite_music_ids")
        val RECENT_MUSIC_IDS = stringPreferencesKey("recent_music_ids")
    }
}

private val Context.musicLibraryDataStore by preferencesDataStore(
    name = "music_library_preferences",
)
