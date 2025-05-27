package io.krakau.genaifinder.service.api

import io.krakau.genaifinder.service.api.model.data.Asset
import io.krakau.genaifinder.service.api.model.data.ExplainedIscc
import io.krakau.genaifinder.service.api.model.data.Iscc
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("/find/image/{iscc}")
    suspend fun getImageAssets(
        @HeaderMap headers: Map<String, String>,
        @Path("iscc") iscc: String
    ): Response<List<Asset>>

    @GET("/resources/images/{filename}")
    suspend fun getImageResource(
        @HeaderMap headers: Map<String, String>,
        @Path("filename") filename: String
    ): Response<ResponseBody>

    @POST("/iscc/create/{urlBase64}")
    suspend fun createIsccFromUrl(
        @HeaderMap headers: Map<String, String>,
        @Path("urlBase64") urlBase64: String
    ): Response<Iscc>

    @GET("/iscc/explain/{iscc}")
    suspend fun getExplainedIscc(
        @HeaderMap headers: Map<String, String>,
        @Path("iscc") iscc: String
    ): Response<ExplainedIscc>
}