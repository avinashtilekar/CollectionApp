package com.example.ajspire.collection

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ajspire.collection.base.DataStoreViewModel
import com.example.ajspire.collection.base.MyViewModelFactory
import com.example.ajspire.collection.databinding.ActivityMainBinding
import com.example.ajspire.collection.extensions.appDataStore
import com.example.ajspire.collection.ui.dailog.ToastMessageUtility
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var dataStoreViewModel: DataStoreViewModel
    private lateinit var toastMessageUtility: ToastMessageUtility
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toastMessageUtility = ToastMessageUtility(this)

        dataStoreViewModel =
            ViewModelProvider(this, MyViewModelFactory(this.application, this.appDataStore())).get(
                DataStoreViewModel::class.java
            )
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_entry,R.id.nav_collection_list, R.id.nav_profile, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.appBarMain.toolbar.setTitleTextAppearance(this, R.style.MyToolbarStyleMarathi)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            showLogoutAlert()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showLogoutAlert() {
        val builder = AlertDialog.Builder(this,R.style.AlertDialogTheme)
        //set title for alert dialog
        builder.setTitle(R.string.action_logout)
        //set message for alert dialog
        builder.setMessage(R.string.logout_confirm)

        //performing positive action
        builder.setPositiveButton(R.string.logout_yes) { dialogInterface, which ->
            dataStoreViewModel.updateUserDetails(null)
            toastMessageUtility.showToastMessage(getString(R.string.logout_sucess))
            val myIntent = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(myIntent)
            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(R.string.logout_no) { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.setOnShowListener(DialogInterface.OnShowListener { dialog ->

            val buttonPositive: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonPositive.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorAccent
                )
            )

            val buttonNegative: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonNegative.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.black
                )
            )

        })

        alertDialog.show()
    }
}