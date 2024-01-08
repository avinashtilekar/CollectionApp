package com.example.ajspire.collection.ui

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.PrinterCallBack
import com.example.ajspire.collection.R
import com.example.ajspire.collection.extensions.appDataStore
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.ui.dailog.ToastMessageUtility
import com.example.ajspire.collection.utility.PrinterType
import com.example.ajspire.collection.utility.printers.ExternelPrinterUtility
import com.example.ajspire.collection.utility.printers.Vriddhi_POS_SDK_PrinterUtility
import com.example.ajspire.collection.utility.printers.bt_printer.ThermalPrinterVaiBtUtility
import com.example.ajspire.collection.view_model.DataBaseViewModel
import com.example.ajspire.collection.view_model.DataStoreViewModel
import com.example.ajspire.collection.view_model.DataStoreViewModelFactory
import com.example.ajspire.collection.view_model.EntryViewModelFactory


abstract class BaseFragment : Fragment() {
    lateinit var toastMessageUtility: ToastMessageUtility
    private var thermalPrinterVaiBtUtility: ThermalPrinterVaiBtUtility? = null
    private var vriddhiPOSSDKPrinterUtility: Vriddhi_POS_SDK_PrinterUtility? = null
    private var externelPrinterUtility : ExternelPrinterUtility?=null
    var currentTransactionTableInsert: TransactionTable? = null
    var printViewObject: View?=null
    val roomDBViewModel: DataBaseViewModel by viewModels {
        EntryViewModelFactory((activity?.application as MyApplication).repository)
    }

    val dataStoreViewModel: DataStoreViewModel by viewModels {
        DataStoreViewModelFactory(activity?.application!!, activity?.appDataStore()!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toastMessageUtility = ToastMessageUtility(requireActivity())
        thermalPrinterVaiBtUtility = ThermalPrinterVaiBtUtility(requireActivity())
        vriddhiPOSSDKPrinterUtility = Vriddhi_POS_SDK_PrinterUtility(requireActivity())
        externelPrinterUtility =ExternelPrinterUtility(requireActivity())
    }

    open fun getHtml(source: String?): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }

    //region printer
    fun printReceipt(printerCallBack: PrinterCallBack? = null, rePrint: Boolean? = false) {
        if (rePrint == true) {
            currentTransactionTableInsert?.let {
                roomDBViewModel.updateReprint(it.invoice_number)
            }
        }

        if ((activity?.application as MyApplication).userPrinters == PrinterType.VriddhiDefault) {
            callPrintViaVriddhiPOSPrinter(printerCallBack, rePrint)
        } else if ((activity?.application as MyApplication).userPrinters == PrinterType.VriddhiExternal) {
            //callPrintViaBluetoothThermalPrinter(printerCallBack, rePrint)
            callExternelPrinter()
        } else {
            //callPrintViaBluetoothThermalPrinter()
            printerNotFoundError()
        }
    }

    fun printerNotFoundError() {
        val message =
            getString(R.string.reciept_number) + (activity?.application as MyApplication).invoiceNumberPrefix + currentTransactionTableInsert?.invoice_number + " " + getString(
                R.string.printer_not_found
            )
        android.app.AlertDialog.Builder(context)
            .setTitle(getString(R.string.printer_not_found_error))
            .setMessage(message)
            .setIcon(R.drawable.ic_error)
            .show()
    }

    private fun callPrintViaBluetoothThermalPrinter(
        printerCallBack: PrinterCallBack? = null,
        rePrint: Boolean? = false
    ) {
        activity?.let { activity ->
            currentTransactionTableInsert?.let { transactionTableInsert ->
                val invoiceNumber =
                    (activity.application as MyApplication).invoiceNumberPrefix + (transactionTableInsert.invoice_number)
                thermalPrinterVaiBtUtility?.let {
                    it.printerCallBack = printerCallBack
                    it.invoiceNumber = invoiceNumber
                    it.rePrint = rePrint
                    it.customerName = transactionTableInsert.customer_name
                    it.customerMobileNumber = transactionTableInsert.customer_mobile_number
                    it.amount = transactionTableInsert.amount
                    it.printBluetooth()
                }
            }

        }
    }

    private fun callPrintViaVriddhiPOSPrinter(
        printerCallBack: PrinterCallBack? = null,
        rePrint: Boolean? = false
    ) {
        activity?.let { activity ->
            currentTransactionTableInsert?.let { transactionTableInsert ->
                val invoiceNumber =
                    (activity.application as MyApplication).invoiceNumberPrefix + (transactionTableInsert.invoice_number)

                vriddhiPOSSDKPrinterUtility?.let {
                    it.printerCallBack = printerCallBack
                    it.invoiceNumber = invoiceNumber
                    it.rePrint = rePrint
                    it.customerName = transactionTableInsert.customer_name
                    it.customerMobileNumber = transactionTableInsert.customer_mobile_number
                    it.amount = transactionTableInsert.amount
                    it.printReceipt()
                }
            }

        }
    }

    private fun callExternelPrinter()
    {
        activity?.let { activity ->
            externelPrinterUtility?.let { it.getPairedPrinters() }
        }
    }

    fun showRePrintAlert() {
        val invoiceData =
            (activity?.application as MyApplication).invoiceNumberPrefix + currentTransactionTableInsert!!.invoice_number
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        //set title for alert dialog
        builder.setTitle(getHtml(getString(R.string.reciept_reprint_title, invoiceData)))
        //set message for alert dialog
        builder.setMessage(getHtml(getString(R.string.reciept_reprint_message, invoiceData)))

        //performing positive action
        builder.setPositiveButton(R.string.reciept_reprint) { dialogInterface, which ->
            printReceipt(null, true)
            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(R.string.not_required) { dialogInterface, which ->
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
                    requireContext(),
                    R.color.colorAccent
                )
            )

            val buttonNegative: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonNegative.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )

        })

        alertDialog.show()
    }
    //endregion printer
}