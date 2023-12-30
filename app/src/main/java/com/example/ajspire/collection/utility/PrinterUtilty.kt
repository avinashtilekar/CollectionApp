package com.example.ajspire.collection.utility

import BpPrinter.mylibrary.BpPrinter
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.example.ajspire.collection.room.entity.TransactionTable
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


class PrinterUtilty constructor(var activity: Activity) {
    val DEVICE_MODE_UNKOWN = -1
    val DEVICE_MODE_F100 = 1
    val DEVICE_MODE_F200 = 0
    val DEVICE_MODE_F600_300 = 2

    private lateinit var bpPrinter: BpPrinter
    private lateinit var private: Printer
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
            }

            override fun onFail(var1: Int) {
                Log.e("binding", "onFail")
            }
        })
    }


    fun printReceipt(insertTransactionTable: TransactionTable) {
        try {
            prePrepairePrinter()
            var ret: Int = printer?.open() ?: ErrCode.ERR_SUCCESS
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("open failed" + String.format(" errCode = 0x%x\n", ret))
                return
            }
            ret = printer?.startCaching() ?: ErrCode.ERR_SUCCESS
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret))
                return
            }
            ret = printer?.setGray(3) ?: ErrCode.ERR_SUCCESS
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("startCaching failed" + String.format(" errCode = 0x%x\n", ret))
                return
            }
            val printStatus = PrintStatus()
            ret = printer?.getStatus(printStatus) ?: ErrCode.ERR_SUCCESS
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
            printer?.let { printer ->
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_CENTER)
                printer.printStr("BLUPRINT SMART PRINT\n" + "TWO INCH PRINTER: TEST PRINT\n" + "--------------------------------\n")
                printer.setAlignStyle(AlignStyle.PRINT_STYLE_LEFT)
                printer.printStr(
                    "13 |ColgateGel  |35.00 |02|70.00\n" +
                            "29 |Pears Soap |25.00 |01|25.00\n" +
                            "128|Maggie TS  |36.00 |04|144.00\n" +
                            "29 |Pears Soap |25.00 |01|25.00\n"
                )
                printer.printStr(
                    ("52 |Dairy Milk |20.00 |10|200.00\n" +
                            "128|Maggie TS  |36.00 |04|144.00\n" +
                            "29 |Pears Soap |25.00 |01|25.00\n" +
                            "128|Maggie TS  |36.00 |04|144.00\n")
                )
                printer.printStr(
                    ("88 |Lux Shower |46.00|01|46.00\n" +
                            "15 |Dabur Honey|65.00 |01|65.00\n" +
                            "52 |Dairy Milk |20.00 |10|200.00\n")
                )
                printer.printStr(
                    ("29 |Pears Soap |25.00 |01|25.00\n" +
                            "128|Maggie TS |36.00|04|144.00\n" + "\n")
                )
                ret = printer.usedPaperLenManage
            }



            if (ret < 0) {
                logMsg("getUsedPaperLenManage failed" + String.format(" errCode = 0x%x\n", ret))
            }
            logMsg("UsedPaperLenManage = " + ret + "mm \n")
            var bitmap: Bitmap? = Bitmap.createBitmap(384, 400, Bitmap.Config.RGB_565)
            var k_CurX = 0
            var k_CurY = 0
            val k_TextSize = 24

            paint = Paint()
            paint.textSize = k_TextSize.toFloat()
            paint.setColor(Color.BLACK)

            var canvas: Canvas? = Canvas((bitmap)!!)
            bitmap.eraseColor(Color.parseColor("#FFFFFF"))
            val fm: Paint.FontMetrics = paint.getFontMetrics()
            val k_LineHeight = Math.ceil((fm.descent - fm.ascent).toDouble()).toInt()
            val displayStr = ""
            val lineWidth: Int = getTextWidth(displayStr)
            k_CurX = (384 - lineWidth) / 2
            canvas!!.drawText(
                displayStr,
                k_CurX.toFloat(),
                (k_CurY + k_TextSize).toFloat(),
                paint
            )
            k_CurY += k_LineHeight + 5
            /*  displayStr =  "";
            k_CurX = 0;
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;
            displayStr =  "";
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;

            displayStr =  "";
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;

            displayStr =   "";
            canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;

            displayStr = "";
         //   canvas.drawText(displayStr, k_CurX, k_CurY + k_TextSize, paint);
            k_CurY += k_LineHeight;

*/
            var newbitmap: Bitmap? = Bitmap.createBitmap((bitmap)!!, 0, 0, 384, k_CurY)
            ret = printer?.printBmp(newbitmap) ?: ErrCode.ERR_SUCCESS
            if (ret != ErrCode.ERR_SUCCESS) {
                logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", ret))
                return
            }
            if (!bitmap.isRecycled) {
                val mFreeBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
                canvas.setBitmap(mFreeBitmap)
                canvas = null
                // canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                bitmap.recycle()
                bitmap = null
                paint.typeface = (null)
            }
            if (newbitmap != null && !newbitmap.isRecycled) {
                newbitmap.recycle()
                newbitmap = null
            }
            printer?.print(object : OnPrinterCallback {
                override fun onSuccess() {
                    logMsg("print success\n")
                    printer!!.feed(32)
                }

                override fun onError(i: Int) {
                    logMsg("printBmp failed" + String.format(" errCode = 0x%x\n", i))
                }
            })
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
}