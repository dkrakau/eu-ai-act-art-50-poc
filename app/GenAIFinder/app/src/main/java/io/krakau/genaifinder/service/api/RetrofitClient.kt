package io.krakau.genaifinder.service.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.35.1.167"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /*
    * Use implementation mushi-retrofitclient for debug information about http requests
    * */

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}