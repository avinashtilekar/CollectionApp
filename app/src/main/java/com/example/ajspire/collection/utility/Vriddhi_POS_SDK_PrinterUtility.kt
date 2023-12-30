package com.example.ajspire.collection.utility

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.example.ajspire.collection.R
import com.ftpos.library.smartpos.buzzer.Buzzer
import com.ftpos.library.smartpos.crypto.Crypto
import com.ftpos.library.smartpos.device.Device
import com.ftpos.library.smartpos.errcode.ErrCode
import com.ftpos.library.smartpos.icreader.IcReader
import com.ftpos.library.smartpos.keymanager.KeyManager
import com.ftpos.library.smartpos.led.Led
import com.ftpos.library.smartpos.magreader.MagReader
import com.ftpos.library.smartpos.memoryreader.MemoryReader
import com.ftpos.library.smartpos.nfcreader.NfcReader
import com.ftpos.library.smartpos.printer.AlignStyle
import com.ftpos.library.smartpos.printer.OnPrinterCallback
import com.ftpos.library.smartpos.printer.PrintStatus
import com.ftpos.library.smartpos.printer.Printer
import com.ftpos.library.smartpos.psamreader.PsamReader
import com.ftpos.library.smartpos.servicemanager.OnServiceConnectCallback
import com.ftpos.library.smartpos.servicemanager.ServiceManager
import java.text.SimpleDateFormat
import java.util.Date

//Reff Link https://bluprints.in/downloads/
// "Vriddhi POS SDK 1.1" sdk only

class Vriddhi_POS_SDK_PrinterUtility constructor(var activity: Activity) {
    val DEVICE_MODE_UNKOWN = -1
    val DEVICE_MODE_F100 = 1
    val DEVICE_MODE_F200 = 0
    val DEVICE_MODE_F600_300 = 2

    private lateinit var paint: Paint
    private var mDeviceModel = DEVICE_MODE_UNKOWN

    var keyManager: KeyManager? = null
    var led: Led? = null
    var buzzer: Buzzer? = null
    var psamReader: PsamReader? = null
    var nfcReader: NfcReader? = null
    var icReader: IcReader? = null
    var magReader: MagReader? = null
    var printer: Printer? = null
    var device: Device? = null
    var crypto: Crypto? = null
    var memoryReader: MemoryReader? = null

    lateinit var invoiceNumber: String
    var customerMobileNumber: String? = null
    var customerName: String? = null
    var amount: String? = null

    private fun getDeviceModel(): Int {
        if (mDeviceModel == DEVICE_MODE_UNKOWN) {
//            String deviceModel = android.os.Build.MODEL;
            val deviceModel: String = getSystemProperty("ro.product.model")
            mDeviceModel = if (deviceModel == "F100" || Build.MODEL == "full_k61v1_32_bsp_1g") {
                DEVICE_MODE_F100
            } else if (deviceModel == "F300" || deviceModel == "F600") {
                DEVICE_MODE_F600_300
            } else {
                DEVICE_MODE_F200
            }
        }
        return mDeviceModel
    }

    fun prePrepairePrinter() {
        getDeviceModel()
        ServiceManager.bindPosServer(activity as Context, object : OnServiceConnectCallback {
            override fun onSuccess() {
                led = Led.getInstance(activity)
                buzzer = Buzzer.getInstance(activity)
                psamReader = PsamReader.getInstance(activity)
                nfcReader = NfcReader.getInstance(activity)
                icReader = IcReader.getInstance(activity)
                magReader = MagReader.getInstance(activity)
                printer = Printer.getInstance(activity)
                device = Device.getInstance(activity)
                crypto = Crypto.getInstance(activity)
                memoryReader = MemoryReader.getInstance(activity)
                keyManager = KeyManager.getInstance(activity)
                val packageName: String = activity.applicationContext.getPackageName()
                if (mDeviceModel != DEVICE_MODE_UNKOWN && mDeviceModel != DEVICE_MODE_F100) {
                    //After connecting to the Service, it must be called once to init the KeyManager, no need to call repeatedly
                    keyManager?.let {
                        val ret = it.setKeyGroupName(packageName)
                        if (ret != ErrCode.ERR_SUCCESS) {
                            Log.e(
                                "ERR_SUCCESS", "setKeyGroupName(" + packageName + String.format(
                                    ")failed,  errCode =0x%x",
                                    ret
                                )
                            )
                        }
                    }
                }

                //try for print
                printReceipt()
            }

            override fun onFail(var1: Int) {
                Log.e("binding", "onFail")
            }
        })
    }


    fun printReceipt() {
        try {
            printer?.let { printer ->
                var ret: Int = printer.open()
                if (ret != ErrCode.ERR_SUCCESS) {
                    logMsg("open failed" + String.format(" errCode = 0x%x\n", ret))
                    return
                }
                ret = printer.startCaching()
                if (ret != ErrCode.ERR_SUCCESS) {
                    logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret))
                    return
                }
                ret = printer.setGray(3)
                if (ret != ErrCode.ERR_SUCCESS) {
                    logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret))
                    return
                }
                val printStatus = PrintStatus()
                ret = printer.getStatus(printStatus)
                if (ret != ErrCode.ERR_SUCCESS) {
                    logMsg("getStatus failed" + String.format(" errCode = 0x%x\n", ret))
                    return
                }
                logMsg("Temperature = " + printStatus.getmTemperature() + "\n")
                logMsg("Gray = " + printStatus.getmGray() + "\n")
                if (!printStatus.getmIsHavePaper()) {
                    logMsg("Printer out of paper\n")
                    return
                }
                logMsg("IsHavePaper = true\n")

                printer.setAlignStyle(AlignStyle.PRINT_STYLE_CENTER)
                printer.printBmp(BitmapFactory.decodeResource(activity.resources, R.drawable.client_logo))
                printer.printStr(activity.getString(R.string.client_name)+"\n")
                printer.printStr("===========================\n")

                //Invoice No
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT)
                printer.printStr(activity.getString(R.string.reciept_number))
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_RIGHT)
                printer.printStr(invoiceNumber+"\n")

                //date time
                val format = SimpleDateFormat("dd-MMM-yyyy 'at' hh:mm:ss a")
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT)
                printer.printStr(activity.getString(R.string.date))
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_RIGHT)
                printer.printStr("${format.format(Date())}\n")

                //fee type
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT)
                printer.printStr(activity.getString(R.string.fee_type)+"(${activity.getString(R.string.sq_fit)}) : ")
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_RIGHT)
                printer.printStr(getSqFit(amount)+"\n")

                //Amount
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT)
                printer.printStr(activity.getString(R.string.amount))
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_RIGHT)
                printer.printStr(amount+"\n")

                //Name
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT)
                printer.printStr(activity.getString(R.string.customer_name))
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_RIGHT)
                printer.printStr((customerName?:"NA")+"\n")

                //Name
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT)
                printer.printStr(activity.getString(R.string.customer_mobile_number))
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_RIGHT)
                printer.printStr((customerMobileNumber?:"NA")+"\n")

                printer.printStr("===========================\n")
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_CENTER)
                printer.printStr(activity.getString(R.string.footer_message)+"\n")
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_CENTER)

                ret = printer.usedPaperLenManage




                if (ret < 0) {
                    logMsg("getUsedPaperLenManage failed" + String.format(" errCode = 0x%x\n", ret))
                }
                logMsg("UsedPaperLenManage = " + ret + "mm \n")
                val bitmap: Bitmap = Bitmap.createBitmap(384, 400, Bitmap.Config.RGB_565)
                val k_CurX: Int
                var k_CurY = 0
                val k_TextSize = 24

                paint = Paint()
                paint.textSize = k_TextSize.toFloat()
                paint.setColor(Color.BLACK)

                val canvas = Canvas((bitmap))
                bitmap.eraseColor(Color.parseColor("#FFFFFF"))
                val fm: Paint.FontMetrics = paint.getFontMetrics()
                val k_LineHeight = Math.ceil((fm.descent - fm.ascent).toDouble()).toInt()
                val displayStr = ""
                val lineWidth: Int = getTextWidth(displayStr)
                k_CurX = (384 - lineWidth) / 2
                canvas.drawText(
                    displayStr,
                    k_CurX.toFloat(),
                    (k_CurY + k_TextSize).toFloat(),
                    paint
                )
                k_CurY += k_LineHeight + 5

                val newbitmap: Bitmap = Bitmap.createBitmap((bitmap), 0, 0, 384, k_CurY)
                ret = printer.printBmp(newbitmap)

                if (ret != ErrCode.ERR_SUCCESS) {
                    logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", ret))
                    return
                }
                if (!bitmap.isRecycled) {
                    val mFreeBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
                    canvas.setBitmap(mFreeBitmap)
                    // canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    bitmap.recycle()
                    paint.typeface = (null)
                }
                if (!newbitmap.isRecycled) {
                    newbitmap.recycle()
                }
                printer.print(object : OnPrinterCallback {
                    override fun onSuccess() {
                        logMsg("print success\n")
                        printer.feed(32)
                    }

                    override fun onError(i: Int) {
                        logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", i))
                    }
                })
            } ?: prePrepairePrinter()
        } catch (e: Exception) {
            e.printStackTrace()
            logMsg("print failed$e\n")
        }
    }

    private fun logMsg(message: String) {
        Log.d("log", message)
    }

    private fun getTextWidth(str: String?): Int {
        var iRet = 0
        if (!str.isNullOrEmpty()) {
            val len = str.length
            val widths = FloatArray(len)
            paint.getTextWidths(str, widths)
            for (j in 0 until len) {
                iRet += Math.ceil(widths[j].toDouble()).toInt()
            }
        }
        return iRet
    }

    private fun getSystemProperty(property: String): String {
        try {
            val clazz = Class.forName("android.os.SystemProperties")
            val getter = clazz.getDeclaredMethod("get", String::class.java)
            val value = getter.invoke(null, property) as String
            if (!TextUtils.isEmpty(value)) {
                return value
            }
        } catch (e: java.lang.Exception) {
            Log.e("SDK", "getSystemProperty: Unable to read system properties")
        }
        return Build.MODEL
    }

    private fun getSqFit(amt: String?): String {
        if (amount == activity.getString(R.string.fee_type_24_amt))
            return activity.getString(R.string.fee_type_24)
        else if (amount == activity.getString(R.string.fee_type_24_48_amt))
            return activity.getString(R.string.fee_type_24)
        else if (amount == activity.getString(R.string.fee_type_48_72_amt))
            return activity.getString(R.string.fee_type_48_72_amt)

        return activity.getString(R.string.fee_type_72_100_amt)
    }
}