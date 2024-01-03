package com.example.ajspire.collection

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.api.helper.NetworkResult
import com.example.ajspire.collection.api.helper.ToastTypeFields
import com.example.ajspire.collection.api.model.request.LoginRequest
import com.example.ajspire.collection.databinding.ActivityLoginBinding
import com.example.ajspire.collection.extensions.appDataStore
import com.example.ajspire.collection.ui.dailog.ToastMessageUtility
import com.example.ajspire.collection.utility.AppUtility
import com.example.ajspire.collection.view_model.ApiCallViewModel
import com.example.ajspire.collection.view_model.DataBaseViewModel
import com.example.ajspire.collection.view_model.DataStoreViewModel
import com.example.ajspire.collection.view_model.EntryViewModelFactory
import com.example.ajspire.collection.view_model.MyViewModelFactory


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val apiCallViewModel: ApiCallViewModel by viewModels()
    private lateinit var toastMessageUtility: ToastMessageUtility
    private lateinit var dataStoreViewModel: DataStoreViewModel
    private var lastDB_InvoiceNumber = 0
    private val dataBaseViewModel: DataBaseViewModel by viewModels {
        EntryViewModelFactory((this.application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreViewModel =
            ViewModelProvider(this, MyViewModelFactory(this.application, this.appDataStore())).get(
                DataStoreViewModel::class.java
            )

        toastMessageUtility = ToastMessageUtility(this)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextAppearance(this, R.style.MyToolbarStyleMarathi)
        binding.toolbar.title = getString(R.string.action_sign_in)
        binding.btnLogin.setOnClickListener {
            if (TextUtils.isEmpty(binding.etUsername.text.toString())) {
                toastMessageUtility.showToastMessage(
                    getString(R.string.user_id_error),
                    ToastTypeFields.Error
                )
            } else if (TextUtils.isEmpty(binding.etPassword.text.toString())) {
                toastMessageUtility.showToastMessage(
                    getString(R.string.password_error),
                    ToastTypeFields.Error
                )
            } else {
                callLoginApi(binding.etUsername.text.toString(), binding.etPassword.text.toString())
            }
        }

        binding.txtTitle.text =
            "${BuildConfig.BUILD_DATE_TIME}\n" + BuildConfig.VERSION_NAME + (if (!BuildConfig.BUILD_TYPE_NAME.isNullOrBlank()) " " + BuildConfig.BUILD_TYPE_NAME else "")
        setObserver()

    }

    private fun callMainScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            toastMessageUtility.showToastMessage(getString(R.string.login_sucess))
            val myIntent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(myIntent)
        }, 5000)
    }

    private fun setObserver() {
        apiCallViewModel.response.observe(this) { response ->
            when (response) {

                is NetworkResult.Loading -> {
                    Log.d("Api", "Loading")
                    AppUtility.hideSoftKeyboard(this)
                    binding.llMainContaint.visibility = View.GONE
                    binding.llLoadding.visibility = View.VISIBLE
                }

                is NetworkResult.Success -> {
                    dataStoreViewModel.updateUserDetails(response.data)

                    dataStoreViewModel.updateInvoicePrefix(response.data?.user?.prefix ?: "")
                    var invoiceNumberForUpdate = response.data?.user?.invoice_no?.toInt() ?: 0
                    if (lastDB_InvoiceNumber > (response.data?.user?.invoice_no?.toInt() ?: 0)) {
                        invoiceNumberForUpdate = lastDB_InvoiceNumber
                    }
                    dataStoreViewModel.updateLastInvoiceNumber(invoiceNumberForUpdate)

                    callMainScreen()
                }

                is NetworkResult.Error -> {
                    binding.llMainContaint.visibility = View.VISIBLE
                    binding.llLoadding.visibility = View.GONE
                    Log.d("Api", "Error")
                    toastMessageUtility.showToastMessage(
                        getString(R.string.technicale_error),
                        ToastTypeFields.Error
                    )
                }
            }
        }

        dataBaseViewModel.maxInvoiceNumber.observe(this)
        {
            it?.let {
                lastDB_InvoiceNumber=it
            }
        }
    }

    private fun callLoginApi(username: String, password: String) {
        apiCallViewModel.login(LoginRequest(username, password))
    }

}
