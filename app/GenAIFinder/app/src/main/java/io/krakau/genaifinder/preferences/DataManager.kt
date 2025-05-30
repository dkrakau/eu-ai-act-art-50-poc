package io.krakau.genaifinder.preferences

import android.content.SharedPreferences
import androidx.core.content.edit

class DataManager(private val prefs: SharedPreferences) {

    // shared preferences keys
    private val PREF_DARK_MODE: String = "darkMode"
    private val PREF_SERVER_PROVIDER: String = "serverProvider"
    private val PREF_SERVER_URLS: String = "serverUrls"

    private val PREFS_IMAGE_URLS: String ="imageUrls"

    private val PREF_INPUT_IMAGE_URL: String = "inputImageUrl"
    private val PREF_INPUT_IMAGE_CONTENTCODE: String = "inputImageContentCode"
    private val PREF_SELECTED_IMAGE_FILENAME: String = "selectedImageFilename"
    private val PREF_SELECTED_IMAGE_PROVIDER: String = "selectedImageProvider"
    private val PREF_SELECTED_IMAGE_CONTENTCODE: String = "selectedImageContentCode"

    fun setDarkMode(darkMode: Boolean) {
        this.prefs.edit() { putBoolean(PREF_DARK_MODE, false) }
    }
    fun getDarkMode(): Boolean {
        return this.prefs.getBoolean(PREF_DARK_MODE, false)
    }

    fun setServerProviderList(serverProviderList: String) {
        this.prefs.edit() {putString(PREF_SERVER_PROVIDER, serverProviderList)}
    }
    fun getServerProviderList(): String {
        return this.prefs.getString(PREF_SERVER_PROVIDER, "") ?: ""
    }

    fun setServerUrlsList(serverUrlsList: String) {
        this.prefs.edit() {putString(PREF_SERVER_URLS, serverUrlsList)}
    }
    fun getServerUrlsList(): String {
        return this.prefs.getString(PREF_SERVER_URLS, "") ?: ""
    }

    fun setImageUrls(imageUrls: List<String>) {
        this.prefs.edit() {putStringSet(PREFS_IMAGE_URLS, imageUrls.toSet())}
    }
    fun getImageUrls(): Set<String> {
        return this.prefs.getStringSet(PREFS_IMAGE_URLS, emptySet()) ?: emptySet()
    }

    fun setInputImageUrl(inputImageUrl: String) {
        this.prefs.edit() { putString(PREF_INPUT_IMAGE_URL, inputImageUrl) }
    }
    fun getInputImageUrl(): String {
        return this.prefs.getString(PREF_INPUT_IMAGE_URL, "") ?: ""
    }

    fun setInputImageContentCode(inputImageContentCode: String) {
        this.prefs.edit() { putString(PREF_INPUT_IMAGE_CONTENTCODE, inputImageContentCode) }
    }
    fun getInputImageContentCode(): String {
        return this.prefs.getString(PREF_INPUT_IMAGE_CONTENTCODE, "") ?: ""
    }

    fun setSelectedImageFilename(selectedImageFilename: String) {
        this.prefs.edit() { putString(PREF_SELECTED_IMAGE_FILENAME, selectedImageFilename) }
    }
    fun getSelectedImageFilename(): String {
        return this.prefs.getString(PREF_SELECTED_IMAGE_FILENAME, "") ?: ""
    }

    fun setSelectedImageProvider(selectedImageProvider: String) {
        this.prefs.edit() { putString(PREF_SELECTED_IMAGE_PROVIDER, selectedImageProvider) }
    }
    fun getSelectedImageProvider(): String {
        return this.prefs.getString(PREF_SELECTED_IMAGE_PROVIDER, "") ?: ""
    }

    fun setSelectedImageContentCode(selectedImageContentCode: String) {
        this.prefs.edit() { putString(PREF_SELECTED_IMAGE_CONTENTCODE, selectedImageContentCode) }
    }
    fun getSelectedImageContentCode(): String {
        return this.prefs.getString(PREF_SELECTED_IMAGE_CONTENTCODE, "") ?: ""
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