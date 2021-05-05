package com.fauran.diplom.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Preferences {
    val SpotifyToken = stringPreferencesKey("spotify_auth_token")
    val FirebaseToken = stringPreferencesKey("fb_token")

    fun <T>Context.getPreferences(key: Preferences.Key<T>): Flow<T?> {
        return dataStore.data.map {
            it[key]
        }
    }

    suspend fun <T>Context.updatePreferences(key: Preferences.Key<T>, value : T) {
        dataStore.edit { settings ->
            settings[key] = value
        }
    }


}