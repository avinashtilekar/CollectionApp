package com.example.ajspire.collection.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.fragment.app.Fragment
import com.example.ajspire.collection.ui.dailog.ToastMessageUtility


abstract class BaseFragment: Fragment()  {
    lateinit var toastMessageUtility: ToastMessageUtility
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toastMessageUtility = ToastMessageUtility(requireActivity())
    }

    open fun getHtml(source: String?): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }
}