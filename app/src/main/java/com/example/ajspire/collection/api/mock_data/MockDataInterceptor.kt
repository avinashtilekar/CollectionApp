package com.example.ajspire.collection.api.mock_data

import com.example.ajspire.collection.BuildConfig
import com.example.ajspire.collection.api.model.request.DataSyncRequest
import com.example.ajspire.collection.api.model.response.DataSyncResponse
import com.example.ajspire.collection.api.model.response.LoginResponse
import com.example.ajspire.collection.api.model.response.User
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException


class MockDataInterceptor(private val headers: Map<String, String>) : Interceptor {
    private val gson = Gson()
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        headers.entries.forEach {
            val (key, value) = it
            requestBuilder.addHeader(key, value)
        }
        if (BuildConfig.BUILD_TYPE_NAME == "Mock") {
            Thread.sleep(500)
            return getMockResponse(request) ?: chain.proceed(request)
        }
        return chain.proceed(request)
    }

    private fun getMockResponse(request: Request): Response? {
        var mockResponse: Any? = null
        var code = 200
        if (request.method == "GET") {
            //TODO need to add more mock response for GET

        } else if (request.method == "POST") {
            if (request.url.pathSegments.contains("login")) {
                code = 200
                mockResponse = getMockLoginResponse()
            } else if (request.url.pathSegments.contains("store")) {
                code = 200
                mockResponse = getMockDataSyncResponse(request)
            }

        } else if (request.method == "PUT") {
            //TODO need to add more mock response for PUT

        }
        mockResponse?.let {
            return getDataResponse(request, code, it)
        }
        return null
    }

    private fun getDataResponse(request: Request, code: Int, response: Any): Response {
        val responseJson = gson.toJson(response)
        return Response.Builder()
            .code(code)
            .addHeader("Content-Type", "application/json")
            .body(responseJson.toResponseBody("application/json".toMediaType()))
            .message(responseJson)
            .request(request)
            .protocol(Protocol.HTTP_2)
            .build()
    }

    private fun getMockLoginResponse(): LoginResponse {
        return LoginResponse(
            "MockDummy",
            User(
                "MockDummy",
                "MockDummy",
                "MockDummy",
                0,
                "MockDummy",
                "MockDummy",
                "MockDummy",
                "MockDummy",
                1,
                "MockDummy",
                "MockDummy",
                "MOC",
                "0"
            )
        )
    }

    private fun getMockDataSyncResponse(request: Request): List<DataSyncResponse> {
        val dataSyncRequest = gson.fromJson(bodyToString(request.body), DataSyncRequest::class.java)
        val responseList = mutableListOf<DataSyncResponse>()
        dataSyncRequest.data.forEach {
            responseList.add(
                DataSyncResponse(
                    it.mobile_tran_key,
                    it.invoice_number.toInt(),
                    "DummyUser"
                )
            )
        }
        return responseList
    }


    private fun bodyToString(request: RequestBody?): String? {
        return try {
            val buffer = Buffer()
            if (request != null) request.writeTo(buffer) else return ""
            buffer.readUtf8()
        } catch (e: IOException) {
            ""
        }
    }
}