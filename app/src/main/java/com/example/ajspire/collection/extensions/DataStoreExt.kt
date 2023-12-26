package com.example.ajspire.collection.extensions

import android.app.Activity
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ajspire.collection.utility.AppUtility
import kotlinx.coroutines.flow.firstOrNull


val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = AppUtility.DATA_STORE_PREFERENCES_NAME
)
private object PreferencesKeys {
    val USER_DETAILS = stringPreferencesKey(AppUtility.DATA_STORE_KEY_USER_DETAILS)
}
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
    context: Context
) {

    suspend fun updateUserDetails(userDetails: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_DETAILS] = userDetails
        }
    }

    suspend fun getUserDetails() = dataStore.data.firstOrNull()?.get(PreferencesKeys.USER_DETAILS)
}

