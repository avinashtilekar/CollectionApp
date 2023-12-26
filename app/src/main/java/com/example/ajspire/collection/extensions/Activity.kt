package com.example.ajspire.collection.extensions

import android.app.Activity

fun Activity.appDataStore()=UserPreferencesRepository(this.userPreferencesDataStore, this)
