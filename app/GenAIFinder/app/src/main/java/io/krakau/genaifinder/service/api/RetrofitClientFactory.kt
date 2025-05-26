package io.krakau.genaifinder.service.api

import com.caverock.androidsvg.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClientFactory {

    companion object {
        private val clients = mutableMapOf<String, Retrofit>()
        fun getClient(baseUrl: String): Retrofit {
            return clients.getOrPut(baseUrl) {
                createRetrofitClient(baseUrl)
            }
        }

        private fun createRetrofitClient(baseUrl: String): Retrofit {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(createLoggingInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        private fun createLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        }

    }
}