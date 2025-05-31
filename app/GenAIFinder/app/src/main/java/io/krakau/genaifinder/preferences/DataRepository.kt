package io.krakau.genaifinder.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataRepository(private val context: Context) {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "genaifinder_shared_preferences")

    object PreferencesKeys {
        val DARK_MODE =   booleanPreferencesKey("darkMode")
        val SERVER_PROVIDER = stringPreferencesKey("serverProvider")
        val SERVER_URLS = stringPreferencesKey("serverUrls")

        val IMAGE_URLS = stringSetPreferencesKey("imageUrls")

        val INPUT_IMAGE_URL = stringPreferencesKey("inputImageUrl")
        val INPUT_IMAGE_CONTENTCODE = stringPreferencesKey("inputImageContentCode")
        val SELECTED_IMAGE_FILENAME = stringPreferencesKey("selectedImageFilename")
        val SELECTED_IMAGE_PROVIDER = stringPreferencesKey("selectedImageProvider")
        val SELECTED_IMAGE_CONTENTCODE = stringPreferencesKey("selectedImageContentCode")
    }

    suspend fun setDarkMode(darkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = darkMode
        }
    }
    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: false
        }

    suspend fun setServerProviderList(serverProviderList: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SERVER_PROVIDER] = serverProviderList
        }
    }
    val serverProviderList: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SERVER_PROVIDER] ?: ""
        }

    suspend fun setServerUrlsList(serverUrlsList: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SERVER_URLS] = serverUrlsList
        }
    }
    val serverUrlsList: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SERVER_URLS] ?: ""
        }

    suspend fun setImageUrls(imageUrls: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IMAGE_URLS] = imageUrls.toSet()
        }
    }
    val imageUrls: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IMAGE_URLS] ?: emptySet()
        }

    suspend fun setInputImageUrl(inputImageUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.INPUT_IMAGE_URL] = inputImageUrl
        }
    }
    val inputImageUrl: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.INPUT_IMAGE_URL] ?: ""
        }

    suspend fun setInputImageContentCode(inputImageContentCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.INPUT_IMAGE_CONTENTCODE] = inputImageContentCode
        }
    }
    val inputImageContentCode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.INPUT_IMAGE_CONTENTCODE] ?: ""
        }

    suspend fun setSelectedImageFilename(selectedImageFilename: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_IMAGE_FILENAME] = selectedImageFilename
        }
    }
    val selectedImageFilename: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_IMAGE_FILENAME] ?: ""
        }

    suspend fun setSelectedImageProvider(selectedImageProvider: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_IMAGE_PROVIDER] = selectedImageProvider
        }
    }
    val selectedImageProvider: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_IMAGE_PROVIDER] ?: ""
        }

    suspend fun setSelectedImageContentCode(selectedImageContentCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_IMAGE_CONTENTCODE] = selectedImageContentCode
        }
    }
    val selectedImageContentCode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_IMAGE_CONTENTCODE] ?: ""
        }

    fun arrayToString(array: Array<String>): String {
        var string = ""
        for(i in array.indices) {
            if(i == array.size - 1) {
                string += array[i]
            } else {
                string += array[i] + ","
            }
        }
        return string
    }

    fun stringToList(listString: String): List<String> {
        return listString.split(",")
    }

}