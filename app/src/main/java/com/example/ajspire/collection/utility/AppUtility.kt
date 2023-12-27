package com.example.ajspire.collection.utility

import android.app.Activity
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

object AppUtility {

    const val DATA_STORE_PREFERENCES_NAME = "collection_store_preferences"
    const val DATA_STORE_KEY_USER_DETAILS = "user_details"
    const val TRANSACTION_TABLE_NAME = "transaction_table"
    const val ROOM_DB_NAME = "collectionDB"


    fun hideSoftKeyboard(activity: Activity) {
        try {
            if (activity.currentFocus != null) {
                val inputMethodManager = activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken, 0
                )
            } else {
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }
        } catch (e: Exception) {
        }
    }

    val currentDateTime: String
        get() {
            val date = Date()
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            return formatter.format(date)
        }

    fun getMobileTranKey(preFixKey: String?): String {
        var value = if (preFixKey != null) preFixKey + "_" + currentDateTime else currentDateTime
        value=value.replace(" ", "_")
        return value
    }


    fun getFormatedMobileNumber(mobileNumber: String): String {
        var mobileNumber = mobileNumber
        mobileNumber = mobileNumber.replace(" ", "")
        mobileNumber =
            mobileNumber.substring(0, 5) + " " + mobileNumber.substring(5, mobileNumber.length)
        return mobileNumber
    }

    fun getFormatedMobileNumberWithCountryCode(mobileNumber: String): String {
        return "+91 " + getFormatedMobileNumber(mobileNumber)
    }

    @Throws(ParseException::class)
    private fun TimeStampConverter(
        inputFormat: String,
        inputTimeStamp: String, outputFormat: String
    ): String {
        return SimpleDateFormat(outputFormat).format(
            SimpleDateFormat(
                inputFormat
            ).parse(inputTimeStamp)
        )
    }

    fun getUnFormatedMobileNumber(mobileNumber: String): String {
        var mobileNumber = mobileNumber
        mobileNumber = mobileNumber.replace(" ", "")
        mobileNumber = mobileNumber.replace("+91", "")
        return mobileNumber
    }

    fun getFormatedPrice(price: Any): String {
        return getFormatedPrice(price.toString())
    }

    fun getFormatedPrice(price: String): String {
        return "₹ $price"
    }

}
