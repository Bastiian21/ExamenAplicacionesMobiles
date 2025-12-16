package com.example.diariodeviaje.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ID = longPreferencesKey("user_id")
    }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_EMAIL] }

    val userId: Flow<Long?> = context.dataStore.data
        .map { preferences -> preferences[USER_ID] }

    suspend fun saveUser(email: String, id: Long) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
            preferences[USER_ID] = id
        }
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }
}