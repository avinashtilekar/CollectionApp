package com.example.ajspire.collection

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ajspire.collection.view_model.DataStoreViewModel
import com.example.ajspire.collection.view_model.MyViewModelFactory
import com.example.ajspire.collection.databinding.ActivityMainBinding
import com.example.ajspire.collection.extensions.appDataStore

import com.example.ajspire.collection.ui.dailog.ToastMessageUtility
import com.example.ajspire.collection.utility.PrinterType
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var dataStoreViewModel: DataStoreViewModel
    private lateinit var toastMessageUtility: ToastMessageUtility
    private lateinit var navHeader: View
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
        setObserver()
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_entry, R.id.nav_collection_list, R.id.nav_profile, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.appBarMain.toolbar.setTitleTextAppearance(this, R.style.MyToolbarStyleMarathi)
        navHeader = navView.getHeaderView(0)
        dataStoreViewModel.getUserPrinter()

    }

    private fun updateHeader(mobileNumber: String, email: String) {
        (navHeader.findViewById(R.id.tvUserName) as TextView).text = mobileNumber
        (navHeader.findViewById(R.id.tvEmailId) as TextView).text = email
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

    override fun onResume() {
        super.onResume()
        //Refresh user details
        dataStoreViewModel.getUserDetails()
    }

    private fun showLogoutAlert() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        //set title for alert dialog
        builder.setTitle(R.string.action_logout)
        //set message for alert dialog
        builder.setMessage(R.string.logout_confirm)

        //performing positive action
        builder.setPositiveButton(R.string.logout_yes) { dialogInterface, which ->
            clearLoginUserDetails()
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

    private fun clearLoginUserDetails() {
        dataStoreViewModel.updateUserDetails(null)
        dataStoreViewModel.updateLastInvoiceNumber(0)
        dataStoreViewModel.updateInvoicePrefix("")
        (application as MyApplication).loginUserDetails = null
    }

    private fun setObserver() {
        dataStoreViewModel.userDetails.observe(this) { loginResponse ->
            (application as MyApplication).loginUserDetails = (loginResponse)
            dataStoreViewModel.getInvoicePrefix()
            loginResponse?.user?.let {
                updateHeader(it.mobile_no, it.email)
            }
        }
        dataStoreViewModel.invoicePrefix.observe(this) { invoiceNumberPrefix ->
            invoiceNumberPrefix?.let {
                (application as MyApplication).invoiceNumberPrefix = (it)
            }
        }

        dataStoreViewModel.userPrinter.observe(this) { userPrinter ->
            userPrinter?.let {
                when (userPrinter) {
                    PrinterType.VriddhiDefault.printerName -> {
                        (application as MyApplication).userPrinters = PrinterType.VriddhiDefault
                    }

                    PrinterType.VriddhiExternal.printerName -> {
                        (application as MyApplication).userPrinters = PrinterType.VriddhiExternal
                    }

                    PrinterType.ThermalExternal.printerName -> {
                        (application as MyApplication).userPrinters = PrinterType.ThermalExternal
                    }
                }

            }
        }
    }
}