package io.krakau.genaifinder.service.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface ApiService {
    @GET("/find/image")
    suspend fun getImageAssets(
        @HeaderMap headers: Map<String, String>,
        @Query("url") url: String
    ): Response<List<Asset>>
}