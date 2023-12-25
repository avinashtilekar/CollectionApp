package com.example.ajspire.collection.api

import com.example.ajspire.collection.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AppRetrofitBuilder {
    companion object {

        private var okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(getHttpLoggingInterceptor())
            .build()

        private var gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()

        private var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        fun getApiService(): AppEndPointInterface {
            return retrofit.create(AppEndPointInterface::class.java)
        }

        private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
}