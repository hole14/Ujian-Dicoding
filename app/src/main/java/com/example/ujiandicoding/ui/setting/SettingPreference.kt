package com.example.ujiandicoding.ui.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SettingPreference private constructor(private val dataStore: DataStore<Preferences>){
    private val THEME_KEY = booleanPreferencesKey("theme_setting")
    private val REMINDER_KEY = booleanPreferencesKey("reminder_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { prefrence ->
            prefrence[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean){
        dataStore.edit { preference ->
            preference[THEME_KEY] = isDarkModeActive
        }
    }
    suspend fun saveReminderSetting(isReminderActive: Boolean){
        dataStore.edit { preference ->
            preference[REMINDER_KEY] = isReminderActive
        }
    }
    fun getReminderSetting(): Flow<Boolean> {
        return dataStore.data.map { prefrence ->
            prefrence[REMINDER_KEY] ?: false
        }
    }
    companion object{
        @Volatile
        private var INSTANCE: SettingPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreference{
            return INSTANCE ?: synchronized(this){
                val instance = SettingPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}