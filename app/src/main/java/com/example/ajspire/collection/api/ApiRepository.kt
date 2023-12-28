package com.example.ajspire.collection.api

import com.example.ajspire.collection.api.helper.BaseApiResponse
import com.example.ajspire.collection.api.helper.NetworkResult
import com.example.ajspire.collection.api.model.request.DataSyncRequest
import com.example.ajspire.collection.api.model.request.LoginRequest
import com.example.ajspire.collection.api.model.response.DataSyncResponse
import com.example.ajspire.collection.api.model.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class ApiRepository constructor(val appEndPointInterface:AppEndPointInterface) : BaseApiResponse(){

    suspend fun login(request: LoginRequest?): Flow<NetworkResult<LoginResponse>> {
        return flow {
            emit(safeApiCall {appEndPointInterface.login(request) })
        }.flowOn(Dispatchers.IO)
    }
    suspend fun dataSync(request: DataSyncRequest?,token :String): Flow<NetworkResult<List<DataSyncResponse>>> {
        return flow {
            emit(safeApiCall {appEndPointInterface.dataSync(request,token) })
        }.flowOn(Dispatchers.IO)
    }
}