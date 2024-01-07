package com.example.ajspire.collection.utility.printers.bt_printer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ajspire.collection.R
import com.example.ajspire.collection.utility.printers.bt_printer.async.AsyncBluetoothEscPosPrint
import com.example.ajspire.collection.utility.printers.bt_printer.async.AsyncEscPosPrint
import com.example.ajspire.collection.utility.printers.bt_printer.async.AsyncEscPosPrinter
import com.example.ajspire.escp_printer_lib.connection.DeviceConnection
import com.example.ajspire.escp_printer_lib.connection.bluetooth.BluetoothConnection
import com.example.ajspire.escp_printer_lib.connection.bluetooth.BluetoothPrintersConnections
import java.text.SimpleDateFormat
import java.util.Date
// Code Reff https://github.com/DantSu/ESCPOS-ThermalPrinter-Android
class ThermalPrinterVaiBtUtility constructor(
    var activity: Activity
) {
    private var selectedDevice: BluetoothConnection? = null
    private val PERMISSION_BLUETOOTH = 1
    private val PERMISSION_BLUETOOTH_ADMIN = 2
    private val PERMISSION_BLUETOOTH_CONNECT = 3
    private val PERMISSION_BLUETOOTH_SCAN = 4
    private var onBluetoothPermissionsGranted: OnBluetoothPermissionsGranted? = null

    lateinit var invoiceNumber: String
    var customerMobileNumber: String? = null
    var customerName: String? = null
    var amount: String? = null

    private fun browseBluetoothDevice() {
        checkBluetoothPermissions(object : OnBluetoothPermissionsGranted {
            @SuppressLint("MissingPermission")
            override fun onPermissionsGranted() {
                val bluetoothDevicesList: Array<BluetoothConnection> =
                    BluetoothPrintersConnections().list as Array<BluetoothConnection>
                val items =
                    arrayOfNulls<String>(bluetoothDevicesList.size + 1)
                items[0] = "Default printer"
                var i = 0
                for (device in bluetoothDevicesList) {
                    items[++i] = device.device.name
                }
                val alertDialog =
                    AlertDialog.Builder(activity)
                alertDialog.setTitle("Bluetooth printer selection")
                alertDialog.setItems(
                    items
                ) { dialogInterface: DialogInterface?, i1: Int ->
                    val index = i1 - 1
                    selectedDevice = if (index == -1) {
                        null
                    } else {
                        bluetoothDevicesList[index]
                    }
                    /*val button = findViewById<View>(R.id.button_bluetooth_browse) as Button
                    button.text = items[i1]*/
                    items[i1]?.let { Log.d("Printer", it) }
                    printBluetooth()
                }
                val alert = alertDialog.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()
            }
        })
    }

    private fun checkBluetoothPermissions(onBluetoothPermissionsGranted: OnBluetoothPermissionsGranted) {
        this.onBluetoothPermissionsGranted = onBluetoothPermissionsGranted
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf<String>(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf<String>(Manifest.permission.BLUETOOTH_ADMIN),
                PERMISSION_BLUETOOTH_ADMIN
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf<String>(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_BLUETOOTH_CONNECT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf<String>(Manifest.permission.BLUETOOTH_SCAN),
                PERMISSION_BLUETOOTH_SCAN
            )
        } else {
            onBluetoothPermissionsGranted.onPermissionsGranted()
        }
    }

    fun printBluetooth() {
        selectedDevice?.let { selectedDevice ->

            val asyncEscPosPrinterthis = getAsyncEscPosPrinterCollection(selectedDevice)
            val finalAsyncEscPosPrinterthis = asyncEscPosPrinterthis
            this.checkBluetoothPermissions(object : OnBluetoothPermissionsGranted {
                override fun onPermissionsGranted() {
                    AsyncBluetoothEscPosPrint(
                        activity,
                        object :
                            AsyncEscPosPrint.OnPrintFinished() {
                            override fun onError(
                                asyncEscPosPrinter: AsyncEscPosPrinter?,
                                codeException: Int
                            ) {
                                Log.e(
                                    "Async.OnPrintFinished",
                                    "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                                )
                            }

                            override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                                Log.i(
                                    "Async.OnPrintFinished",
                                    "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                                )
                            }
                        }
                    )
                        .execute(finalAsyncEscPosPrinterthis)
                }
            })
        } ?: browseBluetoothDevice()
    }


    @SuppressLint("SimpleDateFormat")
    private fun getAsyncEscPosPrinterCollection(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val format = SimpleDateFormat("dd-MMM-yyyy 'at' hh:mm:ss a")
        val printer =
            AsyncEscPosPrinter(
                printerConnection,
                203,
                48f,
                32
            )
        return printer.addTextToPrint(
            """
            [C]================================
            [C]<b type='double'>${format.format(Date())}</b>
            [C]<b><font size='big'>Receipt No. :</b> ${invoiceNumber}</font>
            [C]<b><font size='big'>Name :</b> ${if (customerName != null) customerName else "NA"}</font>
            [C]<b><font size='big'>Mobile No. :</b> ${if (customerMobileNumber != null) customerMobileNumber else "NA"}</font>
            [C]================================
            """.trimIndent()
        )
    }

    private fun getHeaderImage(): Int {
        if (amount == activity.getString(R.string.fee_type_24_48_amt))
            return R.drawable.heder_40
        if (amount == activity.getString(R.string.fee_type_48_72_amt))
            return R.drawable.heder_60
        if (amount == activity.getString(R.string.fee_type_72_100_amt))
            return R.drawable.header_80
        return R.drawable.heder_20
    }

    interface OnBluetoothPermissionsGranted {
        fun onPermissionsGranted()
    }

}