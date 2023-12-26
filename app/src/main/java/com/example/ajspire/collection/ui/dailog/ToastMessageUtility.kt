package com.example.ajspire.collection.ui.dailog

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.ajspire.collection.R
import com.example.ajspire.collection.ui.utility.AppUtility

class ToastMessageUtility constructor(val activity: Activity) {

    fun showToastMessage(
        message: String?,
        isError :Boolean=false ,
        duration: Int = Toast.LENGTH_LONG
    ) {
        AppUtility(activity).hideSoftKeyboard(activity)
        val inflater = activity.layoutInflater
        val layout: View = inflater.inflate(
            R.layout.toast_message,
            activity.findViewById<View?>(R.id.toast_layout_root) as ViewGroup?
        )
        val tvMessage = layout.findViewById<View>(R.id.tvMessage) as TextView
        val toastLayoutRoot = layout.findViewById<View>(R.id.toast_layout_root) as LinearLayout

        if(isError)
        {
            toastLayoutRoot.setBackgroundResource(R.drawable.toast_backgrount_negative)
        }
        tvMessage.text = message
        val toast = Toast(activity)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = duration
        toast.setView(layout)
        toast.show()
    }
}