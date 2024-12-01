package com.hk210.callmicmonitor.util.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

const val CALLER_ID_KEY = "CALLER_ID_ENABLES"

@Singleton
class DataStoreHelper @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val DATASTORE_NAME = "user_database"
        private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

        val CALLER_ID_ENABLED = booleanPreferencesKey(CALLER_ID_KEY)
    }

    private val CALLER_ID_ENABLED_KEY = CALLER_ID_ENABLED

    /**
     * Read a boolean value from DataStore.
     */
    fun readBoolean(key: Preferences.Key<Boolean>, defaultValue: Boolean): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    /**
     * Write a boolean value to DataStore.
     */
    suspend fun writeBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    /**
     * Get the Caller ID toggle state.
     */
    fun isCallerIdEnabled(): Flow<Boolean> {
        return readBoolean(CALLER_ID_ENABLED_KEY, false)
    }

    /**
     * Set the Caller ID toggle state.
     */
    suspend fun setCallerIdEnabled(enabled: Boolean) {
        writeBoolean(CALLER_ID_ENABLED_KEY, enabled)
    }
}
