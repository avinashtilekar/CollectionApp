package com.example.ajspire.collection.ui.dailog

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.ajspire.collection.R
import com.example.ajspire.collection.api.helper.ToastType
import com.example.ajspire.collection.api.helper.ToastTypeFields
import com.example.ajspire.collection.utility.AppUtility

class ToastMessageUtility constructor(val activity: Activity) {

    fun showToastMessage(
        message: String?,
        toastType:ToastType=ToastTypeFields.Success
    ) {
        val duration: Int = Toast.LENGTH_LONG
        AppUtility.hideSoftKeyboard(activity)
        val inflater = activity.layoutInflater
        val layout: View = inflater.inflate(
            R.layout.toast_message,
            activity.findViewById<View?>(R.id.toast_layout_root) as ViewGroup?
        )
        val tvMessage = layout.findViewById<View>(R.id.tvMessage) as TextView
        val toastLayoutRoot = layout.findViewById<View>(R.id.toast_layout_root) as LinearLayout

        when (toastType) {
            is ToastTypeFields.Error -> {
                toastLayoutRoot.setBackgroundResource(R.drawable.toast_backgrount_negative)
            }
            is ToastTypeFields.Warning ->
            {
                toastLayoutRoot.setBackgroundResource(R.drawable.toast_backgrount_warning)
            }

            else -> {
                toastLayoutRoot.setBackgroundResource(R.drawable.toast_backgrount_positive)
            }
        }

        tvMessage.text = message
        val toast = Toast(activity)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = duration
        toast.setView(layout)
        toast.show()
    }
}

