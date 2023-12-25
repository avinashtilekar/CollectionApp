package com.example.ajspire.collection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ajspire.collection.api.helper.NetworkResult
import com.example.ajspire.collection.api.model.request.LoginRequest
import com.example.ajspire.collection.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextAppearance(this, R.style.MyToolbarStyleMarathi)
        binding.toolbar.title = getString(R.string.action_sign_in)
        binding.btnLogin.setOnClickListener {
            callLoginApi(binding.etUsername.text.toString(), binding.etPassword.text.toString())
        }

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
                    // callMainScreen()
                }

                is NetworkResult.Error -> {
                    Log.d("Api", "Error")
                    // show error message
                }
            }
        }
    }
}
