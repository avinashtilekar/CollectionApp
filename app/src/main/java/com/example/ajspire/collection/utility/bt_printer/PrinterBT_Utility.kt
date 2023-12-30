package com.example.ajspire.collection.utility.bt_printer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ajspire.collection.R
import com.example.ajspire.collection.utility.bt_printer.async.AsyncBluetoothEscPosPrint
import com.example.ajspire.collection.utility.bt_printer.async.AsyncEscPosPrint
import com.example.ajspire.collection.utility.bt_printer.async.AsyncEscPosPrinter
import com.example.ajspire.escp_printer_lib.connection.DeviceConnection
import com.example.ajspire.escp_printer_lib.connection.bluetooth.BluetoothConnection
import com.example.ajspire.escp_printer_lib.connection.bluetooth.BluetoothPrintersConnections
import com.example.ajspire.escp_printer_lib.textparser.PrinterTextParserImg
import java.text.SimpleDateFormat
import java.util.Date

class PrinterBT_Utility constructor(val activity: Activity) {
    private var selectedDevice: BluetoothConnection? = null
    val PERMISSION_BLUETOOTH = 1
    val PERMISSION_BLUETOOTH_ADMIN = 2
    val PERMISSION_BLUETOOTH_CONNECT = 3
    val PERMISSION_BLUETOOTH_SCAN = 4
    var onBluetoothPermissionsGranted: OnBluetoothPermissionsGranted? = null
    fun browseBluetoothDevice() {
        checkBluetoothPermissions(object : OnBluetoothPermissionsGranted {
            override fun onPermissionsGranted() {
                val bluetoothDevicesList: Array<BluetoothConnection> =
                    BluetoothPrintersConnections().getList() as Array<BluetoothConnection>
                if (bluetoothDevicesList != null) {
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
                    }
                    val alert = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()
                }
            }
        })
    }

    fun checkBluetoothPermissions(onBluetoothPermissionsGranted: OnBluetoothPermissionsGranted) {
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

        val asyncEscPosPrinterthis = getAsyncEscPosPrinterCollection(selectedDevice)
        val finalAsyncEscPosPrinterthis = asyncEscPosPrinterthis
        this.checkBluetoothPermissions(object : OnBluetoothPermissionsGranted {
            override fun onPermissionsGranted() {
                AsyncBluetoothEscPosPrint(
                    activity,
                    object : AsyncEscPosPrint.OnPrintFinished() {
                        override fun onError(asyncEscPosPrinter: AsyncEscPosPrinter?, codeException: Int) {
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
    }


    @SuppressLint("SimpleDateFormat")
    fun getAsyncEscPosPrinterCollection(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val format = SimpleDateFormat("dd-MMM-dd-yyyy 'at' hh:mm:ss a")
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        return printer.addTextToPrint(
            """
            [C]**Start**
            [C]<img>${
                PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    activity.getApplicationContext().getResources().getDrawableForDensity(
                        R.drawable.header_rs_20_white_bg,
                        DisplayMetrics.DENSITY_MEDIUM
                    )
                )
            }</img>
            [L]
            [C]<b><font size='big'>Invoice Number :</b> 1045</font>
            
            [C]<b type='double'>${format.format(Date())}</b>
            [C]
            [C]================================
            [C]<img>${
                PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    activity.getApplicationContext().getResources().getDrawableForDensity(
                        R.drawable.footer_white_bg,
                        DisplayMetrics.DENSITY_MEDIUM
                    )
                )
            }</img>
            [C]**End**
            
            """.trimIndent()
        )
    }

    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/
    interface OnBluetoothPermissionsGranted {
        fun onPermissionsGranted()
    }

}