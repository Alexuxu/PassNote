package com.ax.passnote.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class UserSettings(
    val usernameThreshold: Int,
    val passwordLength: Int,
    val useCustomChars: Boolean,
    val customChars: String
)

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val USERNAME_THRESHOLD = intPreferencesKey("username_threshold")
        val PASSWORD_LENGTH = intPreferencesKey("password_length")
        val USE_CUSTOM_CHARS = booleanPreferencesKey("use_custom_chars")
        val CUSTOM_CHARS = stringPreferencesKey("custom_chars")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map {
        val usernameThreshold = it[PreferencesKeys.USERNAME_THRESHOLD] ?: 6
        val passwordLength = it[PreferencesKeys.PASSWORD_LENGTH] ?: 12
        val useCustomChars = it[PreferencesKeys.USE_CUSTOM_CHARS] ?: true // Default to true
        val customChars = it[PreferencesKeys.CUSTOM_CHARS] ?: ".!@#$%*&-"
        UserSettings(usernameThreshold, passwordLength, useCustomChars, customChars)
    }

    suspend fun updateUsernameThreshold(threshold: Int) {
        context.dataStore.edit { it[PreferencesKeys.USERNAME_THRESHOLD] = threshold }
    }

    suspend fun updatePasswordLength(length: Int) {
        context.dataStore.edit { it[PreferencesKeys.PASSWORD_LENGTH] = length }
    }

    suspend fun updateUseCustomChars(use: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.USE_CUSTOM_CHARS] = use }
    }

    suspend fun updateCustomChars(chars: String) {
        context.dataStore.edit { it[PreferencesKeys.CUSTOM_CHARS] = chars }
    }
}
