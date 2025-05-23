package io.krakau.genaifinder.service.api.model.view

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.krakau.genaifinder.service.api.RetrofitClient
import io.krakau.genaifinder.service.api.model.data.Asset
import io.krakau.genaifinder.service.api.model.data.ExplainedIscc
import io.krakau.genaifinder.service.api.model.data.Iscc
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.Base64

class ApiViewModel : ViewModel() {

    // constants
    private val LOG_API_VIEW_MODEL: String = "ApiViewModel"

    private val _assets = MutableLiveData<List<Asset>>()
    val assets: LiveData<List<Asset>> = _assets

    private val _imageResource = MutableLiveData<Bitmap>()
    val imageResource: LiveData<Bitmap> = _imageResource

    private val _iscc = MutableLiveData<Iscc>()
    val iscc: LiveData<Iscc> = _iscc

    private val _explainedIscc = MutableLiveData<ExplainedIscc>()
    val explainedIscc: LiveData<ExplainedIscc> = _explainedIscc

   fun fetchGetImageAssets(iscc: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getImageAssets(mapOf("accept" to "application/json"), iscc)
                if (response.isSuccessful) {
                    _assets.value = response.body()
                } else {
                    // Handle error
                    Log.e(LOG_API_VIEW_MODEL, "API: getImageAssets Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e(LOG_API_VIEW_MODEL, "API: getImageAssets Exception: ${e.message}")
            }
        }
    }

    fun fetchGetImageResource(filename: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getImageResource(mapOf("accept" to "*/*"), filename)
                if (response.isSuccessful) {
                    _imageResource.value = BitmapFactory.decodeStream(response.body()?.byteStream())
                } else {
                    // Handle error
                    Log.e(LOG_API_VIEW_MODEL, "API: getImageAssets Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e(LOG_API_VIEW_MODEL, "API: getImageAssets Exception: ${e.message}")
            }
        }
    }

    fun fetchCreateIsccFromUrl(url: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.createIsccFromUrl(mapOf("accept" to "application/json"), Base64.getEncoder().encodeToString(url.encodeToByteArray()))
                if (response.isSuccessful) {
                    _iscc.value = response.body()
                } else {
                    // Handle error
                    Log.e(LOG_API_VIEW_MODEL, "API: createIsccFromUrl Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e(LOG_API_VIEW_MODEL, "API: createIsccFromUrl Exception: ${e.message}")
            }
        }
    }

    fun fetchGetExplainedIscc(iscc: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getExplainedIscc(mapOf("accept" to "application/json"), iscc)
                if (response.isSuccessful) {
                    _explainedIscc.value = response.body()
                } else {
                    // Handle error
                    Log.e(LOG_API_VIEW_MODEL, "API: getExplainedIscc Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e(LOG_API_VIEW_MODEL, "API: getExplainedIscc Exception: ${e.message}")
            }
        }
    }
}