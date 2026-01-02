package com.sokhanara.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.sokhanara.app.domain.model.Language
import com.sokhanara.app.domain.model.SpeechStyle
import com.sokhanara.app.domain.model.VisualTheme
import com.sokhanara.app.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Preferences Manager
 * مدیریت تنظیمات برنامه
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.PREFERENCES_NAME
)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val dataStore = context.dataStore
    
    // Keys
    private object PreferencesKeys {
        val PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
        val PREFERRED_STYLE = stringPreferencesKey("preferred_style")
        val PREFERRED_THEME = stringPreferencesKey("preferred_theme")
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
        val AUTO_SAVE = booleanPreferencesKey("auto_save")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }
    
    // Read preferences
    val preferredLanguage: Flow<Language> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "Error reading preferences")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val code = preferences[PreferencesKeys.PREFERRED_LANGUAGE] ?: Language.PERSIAN.code
            Language.fromCode(code)
        }
    
    val preferredStyle: Flow<SpeechStyle> = dataStore.data
        .catch { handleException(it) }
        .map { preferences ->
            val code = preferences[PreferencesKeys.PREFERRED_STYLE] ?: SpeechStyle.ROOZEH.code
            SpeechStyle.fromCode(code)
        }
    
    val preferredTheme: Flow<VisualTheme> = dataStore.data
        .catch { handleException(it) }
        .map { preferences ->
            val code = preferences[PreferencesKeys.PREFERRED_THEME] ?: VisualTheme.MOHARRAM.code
            VisualTheme.fromCode(code)
        }
    
    val offlineMode: Flow<Boolean> = dataStore.data
        .catch { handleException(it) }
        .map { preferences ->
            preferences[PreferencesKeys.OFFLINE_MODE] ?: false
        }
    
    val autoSave: Flow<Boolean> = dataStore.data
        .catch { handleException(it) }
        .map { preferences ->
            preferences[PreferencesKeys.AUTO_SAVE] ?: true
        }
    
    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { handleException(it) }
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }
    
    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .catch { handleException(it) }
        .map { preferences ->
            preferences[PreferencesKeys.FIRST_LAUNCH] ?: true
        }
    
    // Write preferences
    suspend fun setPreferredLanguage(language: Language) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREFERRED_LANGUAGE] = language.code
        }
    }
    
    suspend fun setPreferredStyle(style: SpeechStyle) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREFERRED_STYLE] = style.code
        }
    }
    
    suspend fun setPreferredTheme(theme: VisualTheme) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREFERRED_THEME] = theme.code
        }
    }
    
    suspend fun setOfflineMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.OFFLINE_MODE] = enabled
        }
    }
    
    suspend fun setAutoSave(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SAVE] = enabled
        }
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    suspend fun setFirstLaunchComplete() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_LAUNCH] = false
        }
    }
    
    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    private suspend fun handleException(exception: Throwable): Preferences {
        if (exception is IOException) {
            Timber.e(exception, "Error reading preferences")
            return emptyPreferences()
        } else {
            throw exception
        }
    }
}