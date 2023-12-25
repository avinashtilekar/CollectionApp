package com.example.ajspire.collection

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ajspire.collection.api.helper.NetworkResult
import com.example.ajspire.collection.api.model.request.LoginRequest
import com.example.ajspire.collection.databinding.ActivityLoginBinding
import com.example.ajspire.collection.ui.dailog.ToastMessageUtility

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var toastMessageUtility: ToastMessageUtility
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toastMessageUtility = ToastMessageUtility(this)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextAppearance(this, R.style.MyToolbarStyleMarathi)
        binding.toolbar.title = getString(R.string.action_sign_in)
        binding.btnLogin.setOnClickListener {
            if (TextUtils.isEmpty(binding.etUsername.text.toString())) {
                toastMessageUtility.showToastMessage(getString(R.string.user_id_error), true)
            } else if (TextUtils.isEmpty(binding.etPassword.text.toString())) {
                toastMessageUtility.showToastMessage(getString(R.string.password_error), true)
            } else {
                callLoginApi(binding.etUsername.text.toString(), binding.etPassword.text.toString())
            }
        }

        binding.txtTitle.text = getString(R.string.app_name)+ " "+BuildConfig.VERSION_NAME

    }

    private fun callMainScreen() {
        val myIntent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(myIntent)
    }

    private fun callLoginApi(username: String, password: String) {
        loginViewModel.login(LoginRequest(username, password))
        loginViewModel.response.observe(this) { response ->
            when (response) {

                is NetworkResult.Loading -> {
                    Log.d("Api", "Loading")
                    // show a progress bar
                }

                is NetworkResult.Success -> {
                    Log.d("Api", "Success")
                    toastMessageUtility.showToastMessage(getString(R.string.login_sucess))
                    callMainScreen()
                }

                is NetworkResult.Error -> {
                    Log.d("Api", "Error")
                    toastMessageUtility.showToastMessage(getString(R.string.technicale_error), true)
                }
            }
        }
    }
}
