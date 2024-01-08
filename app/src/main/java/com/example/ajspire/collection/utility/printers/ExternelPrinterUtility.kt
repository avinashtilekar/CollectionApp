package com.example.ajspire.collection.utility.printers

import BpPrinter.mylibrary.BluetoothConnectivity
import BpPrinter.mylibrary.BpPrinter
import BpPrinter.mylibrary.Scrybe
import android.Manifest.permission
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.ajspire.collection.R
import java.io.IOException
import java.io.InputStream

//Reff Link https://bluprints.in/downloads/
// "Vriddhi POS SDK 1.1" sdk only

class ExternelPrinterUtility constructor(private var activity: Activity) : Scrybe {
    private var BpScrybeDevice: BluetoothConnectivity? = null
    private var BPprinter: BpPrinter? = null

    private val INITIAL_PERMS = arrayOf(
        permission.BLUETOOTH_SCAN,
        permission.BLUETOOTH,
        permission.BLUETOOTH_CONNECT,
        permission.BLUETOOTH_ADVERTISE,
        permission.BLUETOOTH_ADMIN
    )
    private val INITIAL_REQUEST = 1337
    private var glbPrinterWidth = 32
    private val CONN_TYPE_BT = 1
    private var m_conn_type = CONN_TYPE_BT
    private var printerList = listOf<String>()

    fun getPairedPrinters() {
        BpScrybeDevice = BluetoothConnectivity(this)
        ActivityCompat.requestPermissions(
            activity,
            INITIAL_PERMS,
            INITIAL_REQUEST
        )
        if (m_conn_type == CONN_TYPE_BT) {
            printerList = BpScrybeDevice!!.pairedPrinters as List<String>
            if (printerList.isNotEmpty()) {
                showPrinterList()
            } else showAlert("No Paired Printers found")
        }
    }

    private fun showPrinterList() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose an printer")
        builder.setItems(
            printerList.toTypedArray()
        ) { dialog, which ->
            Log.d("Selected Printer ", printerList[which])
            onConnectWithPrinter(printerList[which])
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun onConnectWithPrinter(printerName: String) {
        try {
            BpScrybeDevice!!.connectToPrinter(printerName)
            BPprinter = BpScrybeDevice!!.aemPrinter
           // showAlert("Connected with $printerName")
            printingSample()

        } catch (e: IOException) {
            if (e.message!!.contains("Service discovery failed")) {
                showAlert("Not Connected\n$printerName is unreachable or off otherwise it is connected with other device")
            } else if (e.message!!.contains("Device or resource busy")) {
                showAlert("the device is already connected")
            } else {
                showAlert("Unable to connect")
            }
        }
    }

    override fun onDiscoveryComplete(btPrinters: ArrayList<String>?) {
        Log.d("Not Usefull", "Not Usefull")
    }


    private fun printingSample() {

        val inputBitmapHeader: Bitmap? = drawTextToBitmap(R.drawable.header_latest, "अविनाश टिळेकर")
        if (glbPrinterWidth == 32) {
            BPprinter!!.POS_Set_Text_alingment(0x01.toByte())
            BPprinter!!.printImage(inputBitmapHeader, 0)
            BPprinter!!.setCarriageReturn()
            BPprinter!!.Initialize_Printer()
        } else {
            BPprinter!!.POS_Set_Text_alingment(0x01.toByte())
            BPprinter!!.printImage(inputBitmapHeader, 1)
            BPprinter!!.setCarriageReturn()
            BPprinter!!.Initialize_Printer()
        }
        //Disconnect once print done
        BpScrybeDevice!!.disConnectPrinter()

    }

    private fun drawTextToBitmap(
        gResId: Int,
        gText: String
    ): Bitmap? {
        val resources: Resources = activity.resources
        val scale = resources.displayMetrics.density
        //Bitmap bitmap = Bitmap.createBitmap((int)(100*scale), (int)(70*scale), Bitmap.Config.ARGB_8888);
        val bitmapResource = BitmapFactory.decodeResource(resources, gResId)
        var bitmap = Bitmap.createBitmap(
            bitmapResource.width,
            (bitmapResource.width * 0.70).toInt(),
            Bitmap.Config.ARGB_8888
        )
        bitmap.eraseColor(Color.WHITE)
        var bitmapConfig = bitmap.config
        // set default bitmap config if none
        bitmapConfig = Bitmap.Config.ARGB_8888

        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(bitmap)
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        paint.color = Color.rgb(61, 61, 61)
        // text size in pixels
        val textSize = (22 * scale).toInt().toFloat()
        val lineHeight = (textSize * 1.50).toFloat()
        paint.textSize = textSize
        // text shadow
        paint.setShadowLayer(3f, 0f, 3f, Color.WHITE)
        //set font
        val font = Typeface.createFromAsset(activity.getAssets(), "fonts/shivaji01_normal.ttf")
        val typeface = Typeface.create(font, Typeface.BOLD)
        paint.typeface = typeface
        // draw text to the Canvas center
        val bounds = Rect()
        paint.getTextBounds(gText, 0, gText.length, bounds)
        val deviceX = bitmap.width - bounds.width()
        val deviceY = bitmap.height - bounds.height()
        val x = 5f
        var y = (10 + (22 * scale).toInt()).toFloat()
        //First Row
        canvas.drawText(gText, x, y, paint)
        canvas.drawText(gText, (deviceX - gText.length).toFloat(), y, paint)

        //second row
        for (i in 0..9) {
            y = y + lineHeight
            canvas.drawText("$gText:$i", x, y, paint)
            canvas.drawText(gText, (deviceX - gText.length).toFloat(), y, paint)
        }
        return bitmap
    }

    private fun showAlert(alertMsg: String?) {
        val alertBox = AlertDialog.Builder(activity)
        alertBox.setMessage(alertMsg).setCancelable(false).setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which -> // TODO Auto-generated method stub
                return@OnClickListener
            })
        val alert = alertBox.create()
        alert.show()
    }
}