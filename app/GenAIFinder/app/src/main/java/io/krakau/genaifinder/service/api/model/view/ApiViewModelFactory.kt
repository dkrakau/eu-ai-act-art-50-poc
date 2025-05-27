package io.krakau.genaifinder.service.api.model.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class ApiViewModelFactory(
    private val serverProvider: List<String>,
    private val serverUrls: List<String>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApiViewModel(serverProvider, serverUrls) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}