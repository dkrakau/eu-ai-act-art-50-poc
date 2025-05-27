package io.krakau.genaifinder.service.api.model.view

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.krakau.genaifinder.service.api.ApiService
import io.krakau.genaifinder.service.api.RetrofitClient
import io.krakau.genaifinder.service.api.RetrofitClientFactory
import io.krakau.genaifinder.service.api.model.data.Asset
import io.krakau.genaifinder.service.api.model.data.ExplainedIscc
import io.krakau.genaifinder.service.api.model.data.Iscc
import kotlinx.coroutines.launch
import kotlinx.coroutines.processNextEventInCurrentThread
import java.io.InputStream
import java.util.Base64

class ApiViewModel(
    private var serverProvider: List<String>,
    private var serverUrls: List<String>
) : ViewModel() {

    // constants
    private val LOG_API_VIEW_MODEL: String = "ApiViewModel"

    private var apiServices: MutableList<ApiService> = mutableListOf()

    private val _assets = MutableLiveData<List<Asset>>()
    val assets: LiveData<List<Asset>> = _assets

    private val _imageResource = MutableLiveData<Bitmap>()
    val imageResource: LiveData<Bitmap> = _imageResource

    private val _iscc = MutableLiveData<Iscc>()
    val iscc: LiveData<Iscc> = _iscc

    private val _explainedIscc = MutableLiveData<ExplainedIscc>()
    val explainedIscc: LiveData<ExplainedIscc> = _explainedIscc

    init {
        for(serverUrl in serverUrls) {
            apiServices.add(RetrofitClientFactory.getClient(serverUrl).create(ApiService::class.java))
        }
    }

   fun fetchGetImageAssets(iscc: String) {
        viewModelScope.launch {
            try {
                /*
                 * this call will be executed multiple times and
                 * gets combined as one sorted list inside activity
                 */
                for(apiService in apiServices) {
                    val response = apiService.getImageAssets(mapOf("accept" to "application/json"), iscc)
                    if (response.isSuccessful) {
                        Log.d(LOG_API_VIEW_MODEL, "" + response.body())
                        _assets.value = response.body()
                    } else {
                        // Handle error
                        Log.e(LOG_API_VIEW_MODEL, "API: getImageAssets Error: ${response.code()} - ${response.message()}")
                        _assets.value = emptyList()
                    }
                }
                Log.d(LOG_API_VIEW_MODEL, "" + _assets.value.toString())
            } catch (e: Exception) {
                // Handle exception
                Log.e(LOG_API_VIEW_MODEL, "API: getImageAssets Exception: ${e.message}")
                _assets.value = emptyList()
            }
        }
    }

    fun fetchGetImageResource(provider: String, filename: String) {
        viewModelScope.launch {
            try {

                /* FOR DEVELOPMENT: One host simulating multiple provider */
                val providerApiService = apiServices[serverProvider.indexOf("genaifinder")]
                //val providerApiService = apiServices[serverProvider.indexOf(provider)]

                val response = providerApiService.getImageResource(mapOf("accept" to "*/*"), filename)
                if (response.isSuccessful) {
                    _imageResource.value = BitmapFactory.decodeStream(response.body()?.byteStream())
                } else {
                    // Handle error
                    Log.e(LOG_API_VIEW_MODEL, "API: getImageResource Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e(LOG_API_VIEW_MODEL, "API: getImageResource Exception: ${e.message}")
            }
        }
    }

    fun fetchCreateIsccFromUrl(url: String) {
        viewModelScope.launch {
            var abort = false
            var randomIndex = getRandomIndex(apiServices.size)
            while(!abort) {
                try {
                    val randomApiService = apiServices[randomIndex]
                    val response = randomApiService.createIsccFromUrl(mapOf("accept" to "application/json"), Base64.getEncoder().encodeToString(url.encodeToByteArray()))
                    if (response.isSuccessful) {
                        _iscc.value = response.body()
                        abort = true
                    } else {
                        // Handle error
                        Log.e(LOG_API_VIEW_MODEL, "API: createIsccFromUrl Error: ${response.code()} - ${response.message()}")
                    }
                } catch (e: Exception) {
                    // Handle exception
                    Log.e(LOG_API_VIEW_MODEL, "API: createIsccFromUrl Exception: ${e.message}")
                    if(apiServices.size > 0) {
                        apiServices.removeAt(randomIndex)
                        randomIndex = getRandomIndex(apiServices.size)
                    } else {
                        abort = true
                    }
                }
            }
        }
    }

    fun fetchGetExplainedIscc(iscc: String) {
        viewModelScope.launch {
            var abort = false
            var randomIndex = getRandomIndex(apiServices.size)
            while (!abort) {
                try {
                    val randomApiService = apiServices[randomIndex]
                    val response = randomApiService.getExplainedIscc(mapOf("accept" to "application/json"), iscc)
                    if (response.isSuccessful) {
                        _explainedIscc.value = response.body()
                        abort = true
                    } else {
                        // Handle error
                        Log.e(LOG_API_VIEW_MODEL,"API: getExplainedIscc Error: ${response.code()} - ${response.message()}")
                    }
                } catch (e: Exception) {
                    // Handle exception
                    Log.e(LOG_API_VIEW_MODEL, "API: getExplainedIscc Exception: ${e.message}")
                    if(apiServices.size > 0) {
                        apiServices.removeAt(randomIndex)
                        randomIndex = getRandomIndex(apiServices.size)
                    } else {
                        abort = true
                    }
                }
            }
        }
    }

    private fun getRandomIndex(apiServicesSize: Int): Int {
        var randomIndex = 0
        if(apiServicesSize > 0) {
            randomIndex = (0 until apiServicesSize).random()
        }
        return randomIndex
    }
}