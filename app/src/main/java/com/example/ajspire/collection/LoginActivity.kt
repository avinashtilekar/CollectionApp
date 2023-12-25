package com.example.ajspire.collection

import android.R.attr.value
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ajspire.collection.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextAppearance(this,R.style.MyToolbarStyleMarathi)
        binding.toolbar.title=getString(R.string.action_sign_in)
        binding.btnLogin.setOnClickListener {
            callMainScreen()
        }
    }

    private fun callMainScreen()
    {
        val myIntent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(myIntent)
    }
}
