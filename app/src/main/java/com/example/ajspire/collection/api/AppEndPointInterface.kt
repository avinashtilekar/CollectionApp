package com.example.ajspire.collection.api

import com.example.ajspire.collection.api.model.request.DataSyncRequest
import com.example.ajspire.collection.api.model.request.LoginRequest
import com.example.ajspire.collection.api.model.response.DataSyncResponse
import com.example.ajspire.collection.api.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface AppEndPointInterface {
    @Headers("Content-Type: application/json")
    @POST("api/user/login")
    suspend fun login(@Body user: LoginRequest?): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("api/trans/store")
    suspend fun dataSync(@Body dataSyncRequest: DataSyncRequest?): Response<DataSyncResponse>
}