package com.example.ajspire.collection.extensions

import android.app.Activity
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.api.model.response.LoginResponse
import java.text.SimpleDateFormat
import java.util.Date

fun Activity.appDataStore() = UserPreferencesRepository(this.userPreferencesDataStore, this)
fun Activity.getLoginUserDetails() = (application as MyApplication).loginUserDetails
fun Activity.setLoginUserDetails(loginResponse: LoginResponse?) {
    (application as MyApplication).loginUserDetails = loginResponse
}

