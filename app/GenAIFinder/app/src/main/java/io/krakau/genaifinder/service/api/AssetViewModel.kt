package io.krakau.genaifinder.service.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AssetViewModel : ViewModel() {

    // constants
    private val LOG_ASSET_VIEW_MODEL: String = "AssetViewModel"

    private val _assets = MutableLiveData<List<Asset>>()
    val assets: LiveData<List<Asset>> = _assets

    fun fetchImageAssets(url: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getImageAssets(mapOf("accept" to "application/json"), url)
                if (response.isSuccessful) {
                    _assets.value = response.body()
                } else {
                    // Handle error
                    Log.e(LOG_ASSET_VIEW_MODEL, "API: Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e(LOG_ASSET_VIEW_MODEL, "API: Exception: ${e.message}")
            }
        }
    }
}