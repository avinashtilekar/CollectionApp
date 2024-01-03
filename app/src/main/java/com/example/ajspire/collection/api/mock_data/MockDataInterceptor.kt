package com.example.ajspire.collection.api.mock_data

import com.example.ajspire.collection.BuildConfig
import com.example.ajspire.collection.api.model.response.LoginResponse
import com.example.ajspire.collection.api.model.response.User
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockDataInterceptor(private val headers: Map<String, String>) : Interceptor {
    private val gson = Gson()
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        val uriPath = request.url.toUri().path

        headers.entries.forEach {
            val (key, value) = it
            requestBuilder.addHeader(key, value)
        }
        if (BuildConfig.BUILD_TYPE_NAME == "Mock") {
            Thread.sleep(500)
            return getMockResponse(request)
        }
        return chain.proceed(request)
    }

    private fun getMockResponse(request: Request): Response {
        var mockResponse :Any?=null
        var code = 200
        if (request.method == "GET") {
            //TODO need to add more mock response for GET

        } else if (request.method == "POST") {
            code = 200
            if (request.url.pathSegments.contains("login")) {
                mockResponse=getMockLoginResponse()
            }

        } else if (request.method == "PUT") {
            //TODO need to add more mock response for PUT

        }

        return getDataResponse(request, code,mockResponse!!)
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
}