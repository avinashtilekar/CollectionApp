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
import com.example.ajspire.collection.model.PrintDataModel
import java.io.IOException

//Reff Link https://bluprints.in/downloads/
// "Vriddhi POS SDK 1.1" sdk only

class ExternelPrinterUtility constructor(private var activity: Activity) : Scrybe {
    private var BpScrybeDevice: BluetoothConnectivity? = null
    private var BPprinter: BpPrinter? = null
    private val lineBreaker = " _______________________________\n"
    private val lineEmpty = " \n"
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

    lateinit var invoiceNumber: String
    var customerMobileNumber: String? = null
    var customerName: String? = null
    var amount: String? = null

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

    private fun getPrintData(): List<PrintDataModel> {
        val printData = mutableListOf<PrintDataModel>()
        printData.add(PrintDataModel(activity.getString(R.string.reciept_number), invoiceNumber))
        printData.add(
            PrintDataModel(
                activity.getString(R.string.customer_name),
                (customerName ?: "NA")
            )
        )
        printData.add(
            PrintDataModel(
                activity.getString(R.string.customer_mobile_number1),
                (customerMobileNumber ?: "NA")
            )
        )
        printData.add(
            PrintDataModel(
                activity.getString(R.string.fee_type) + " (${
                    activity.getString(
                        R.string.sq_fit
                    )
                })", getSqFit(amount)
            )
        )
        printData.add(
            PrintDataModel(
                activity.getString(R.string.amount), amount
            )
        )
        return printData
    }
    private fun getFoterNoteData(): List<PrintDataModel> {
        val printData = mutableListOf<PrintDataModel>()
        printData.add(PrintDataModel(activity.getString(R.string.footer_message1), null))
        printData.add(PrintDataModel(activity.getString(R.string.footer_message2), null))
        printData.add(PrintDataModel(activity.getString(R.string.footer_message3), null))
        printData.add(PrintDataModel(activity.getString(R.string.footer_message4), null))

        return printData
    }

    private fun printingSample() {
        val headerBitmap: Bitmap = getDrawableToBitmap(R.drawable.header_latest_white)
        val footerBitmap: Bitmap = getDrawableToBitmap(R.drawable.ic_latest_footer_white)
        val inputBitmapDetails: Bitmap? = drawTextToBitmap(R.drawable.header_latest, getPrintData())
        val inputBitmapFoterNote: Bitmap? = drawTextToBitmap(R.drawable.header_latest, getFoterNoteData())
        if (glbPrinterWidth == 32) {
            BPprinter!!.POS_Set_Text_alingment(0x01.toByte())
            BPprinter!!.printImage(headerBitmap, 0)
            BPprinter!!.POS_Set_Text_alingment(0x01.toByte())
            BPprinter!!.print(lineBreaker)
            BPprinter!!.printImage(inputBitmapDetails, 0)
            BPprinter!!.print(lineBreaker)
            BPprinter!!.print(activity.getString(R.string.powered_by) + "\n")
            BPprinter!!.printImage(footerBitmap, 0)
            BPprinter!!.printImage(inputBitmapFoterNote, 0)
            BPprinter!!.print(lineEmpty)
            BPprinter!!.print(lineEmpty)
            BPprinter!!.setCarriageReturn()
            BPprinter!!.Initialize_Printer()
        } else {
            BPprinter!!.POS_Set_Text_alingment(0x01.toByte())
            BPprinter!!.printImage(headerBitmap, 1)
            BPprinter!!.setCarriageReturn()
            BPprinter!!.Initialize_Printer()
        }
        //Disconnect once print done
        BpScrybeDevice!!.disConnectPrinter()

    }

    private fun drawTextToBitmap(
        gResId: Int,
        printDataList: List<PrintDataModel>,
    ): Bitmap? {
        val resources: Resources = activity.resources
        val scale = resources.displayMetrics.density
        //Bitmap bitmap = Bitmap.createBitmap((int)(100*scale), (int)(70*scale), Bitmap.Config.ARGB_8888);
        val bitmapResource = BitmapFactory.decodeResource(resources, gResId)
        val canvasHight=((bitmapResource.width * 0.15)*(printDataList.size*0.70)).toInt()
        var bitmap = Bitmap.createBitmap(
            bitmapResource.width,
            canvasHight,
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
        val textSize = (35 * scale).toInt().toFloat()
        val lineHeight = (textSize * 1.50).toFloat()
        paint.textSize = textSize
        // text shadow
        paint.setShadowLayer(3f, 0f, 3f, Color.WHITE)
        //set font
        val font = Typeface.createFromAsset(activity.getAssets(), "fonts/shivaji01_normal.ttf")
        val typeface = Typeface.create(font, Typeface.BOLD)
        //paint.typeface = typeface
        //draw text to the Canvas center

        val deviceX = bitmap.width
        val deviceY = bitmap.height
        val x = 5f
        var y = (10 + (22 * scale).toInt()).toFloat()

        //second row
        printDataList.forEach {
            y += lineHeight
            canvas.drawText(it.value1, x, y, paint)

            it.value2?.let {
                // draw text to the Canvas center
                val bounds = Rect()
                paint.getTextBounds(it, 0, it.length, bounds)
                canvas.drawText(it+" ", (((deviceX-bounds.width())-it.length).toFloat()), y, paint)
            }
        }
        return bitmap
    }

    private fun getDrawableToBitmap(resourcesId: Int): Bitmap {
        return BitmapFactory.decodeResource(activity.resources, resourcesId)
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

    private fun getSqFit(amt: String?): String {
        if (amount == activity.getString(R.string.fee_type_24_amt))
            return activity.getString(R.string.fee_type_24)
        else if (amount == activity.getString(R.string.fee_type_24_48))
            return activity.getString(R.string.fee_type_24)
        else if (amount == activity.getString(R.string.fee_type_48_72))
            return activity.getString(R.string.fee_type_48_72)

        return activity.getString(R.string.fee_type_72_100)
    }
}