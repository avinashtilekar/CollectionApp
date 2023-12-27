package com.example.ajspire.collection

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.base.DataStoreViewModel
import com.example.ajspire.collection.base.MyViewModelFactory
import com.example.ajspire.collection.databinding.ActivitySplashBinding
import com.example.ajspire.collection.extensions.appDataStore
import com.example.ajspire.collection.extensions.setLoginUserDetails

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var dataStoreViewModel: DataStoreViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataStoreViewModel =
            ViewModelProvider(this, MyViewModelFactory(this.application, this.appDataStore())).get(
                DataStoreViewModel::class.java
            )
        setObserver()

        Handler(Looper.getMainLooper()).postDelayed({
            dataStoreViewModel.getUserDetails()
        }, 5000)

        binding.txtHeading.text = getString(R.string.client_name)
        binding.txtTitle.text = getString(R.string.app_name)+ " "+BuildConfig.VERSION_NAME
        binding.txtFooter.text = "${BuildConfig.BUILD_DATE_TIME} \n\n${getString(R.string.ajspire_tec)}"
    }

    private fun setObserver() {
        dataStoreViewModel.userDetails.observe(this) { loginResponse ->
            val myIntent = Intent(
                this,
                if (loginResponse == null) LoginActivity::class.java else MainActivity::class.java
            )
            setLoginUserDetails(loginResponse)
            finish()
            startActivity(myIntent)
        }
    }
}