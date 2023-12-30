package com.example.ajspire.collection.ui.entry

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.R
import com.example.ajspire.collection.databinding.FragmentEntryBinding
import com.example.ajspire.collection.extensions.appDataStore
import com.example.ajspire.collection.extensions.startBlinkAnimation
import com.example.ajspire.collection.extensions.stopBlinkAnimation
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.ui.custom.RadioGridGroup
import com.example.ajspire.collection.view_model.DataBaseViewModel
import com.example.ajspire.collection.view_model.EntryViewModelFactory
import com.example.ajspire.collection.utility.AppUtility
import com.example.ajspire.collection.utility.PrinterUtilty
import com.example.ajspire.collection.view_model.DataStoreViewModel
import com.example.ajspire.collection.view_model.DataStoreViewModelFactory

class EntryFragment : Fragment() {

    private var _binding: FragmentEntryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val dataBaseViewModel: DataBaseViewModel by viewModels {
        EntryViewModelFactory((activity?.application as MyApplication).repository)
    }
    private var selectedFeeType = "24"
    private var lastInvoiceNumber = 0
    private val dataStoreViewModel: DataStoreViewModel by viewModels {
        DataStoreViewModelFactory(activity?.application!!, activity?.appDataStore()!!)
    }

    private lateinit var printerUtilty: PrinterUtilty

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        printerUtilty=PrinterUtilty(requireActivity())
        setObserver()
        updateUi()
        return binding.root
    }

    private fun setObserver() {
        dataBaseViewModel.allTransactions.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("Data Found", it.toString())
            }
        })
        dataStoreViewModel.lastInvoiceNumber.observe(viewLifecycleOwner) { lastInvoiceNumberFromStore ->
            lastInvoiceNumberFromStore?.let {
                lastInvoiceNumber = it
                binding.etInvoiceNumber.setText((activity?.application as MyApplication).invoiceNumberPrefix + (lastInvoiceNumber + 1))
            }
        }
        dataStoreViewModel.getLastInvoiceNumber()
    }

    private fun updateUi() {
        binding.apply {
            rgFeeType.setOnCheckedChangeListener(object : RadioGridGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGridGroup?, checkedId: Int) {
                    AppUtility.hideSoftKeyboard(activity as Activity)
                    when (checkedId) {
                        R.id.rb24 -> {
                            selectedFeeType = getString(R.string.fee_type_24)
                            etAmount.setText(R.string.fee_type_24_amt)
                        }

                        R.id.rb24X48 -> {
                            selectedFeeType = getString(R.string.fee_type_24_48)
                            etAmount.setText(R.string.fee_type_24_48_amt)
                        }

                        R.id.rb48X72 -> {
                            selectedFeeType = getString(R.string.fee_type_48_72)
                            etAmount.setText(R.string.fee_type_48_72_amt)
                        }

                        R.id.rb72X100 -> {
                            selectedFeeType = getString(R.string.fee_type_48_72)
                            etAmount.setText(R.string.fee_type_72_100_amt)
                        }
                    }
                }

            })
            btnSubmit.setOnClickListener {
                val entryInvoiceNumber = lastInvoiceNumber + 1
                AppUtility.hideSoftKeyboard(requireActivity())

               val insertTransactionTable= TransactionTable(
                    fee_type = selectedFeeType,
                    amount = etAmount.text.toString(),
                    mobile_tran_key = AppUtility.getMobileTranKey(),
                    invoice_number = entryInvoiceNumber,
                    customer_name = if (etUserName.text.toString()
                            .isNotEmpty()
                    ) etUserName.text.toString() else null,
                    customer_mobile_number = if (etMobileNumber.text.toString()
                            .isNotEmpty()
                    ) etMobileNumber.text.toString() else null
                )

                dataBaseViewModel.insert(insertTransactionTable)

                updateLastInvoiceNumberToStoreDate(entryInvoiceNumber)
                showConfirmAlert(insertTransactionTable)
                reSetScreen()
               }
            btnCancel.setOnClickListener {
                reSetScreen()
            }

            etUserName.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    tvKeyBoardWarning.startBlinkAnimation()
                } else {
                    tvKeyBoardWarning.stopBlinkAnimation()
                    tvKeyBoardWarning.visibility = View.GONE
                }
            }
        }
    }

    private fun updateLastInvoiceNumberToStoreDate(entryInvoiceNumber: Int) {
        lastInvoiceNumber = entryInvoiceNumber
        dataStoreViewModel.updateLastInvoiceNumber(lastInvoiceNumber)
    }

    private fun reSetScreen() {
        binding.apply {
            etMobileNumber.setText("")
            etUserName.setText("")
            rb24.isChecked = true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showConfirmAlert(insertTransactionTable: TransactionTable) {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        //set title for alert dialog
        builder.setTitle(R.string.menu_entry)
        //set message for alert dialog
        builder.setMessage(R.string.save_sucess_message)

        //performing positive action
        builder.setPositiveButton(R.string.close) { dialogInterface, which ->
            printerUtilty.prePrepairePrinter()
           // printerUtilty.printReceipt(insertTransactionTable)
            dataStoreViewModel.getLastInvoiceNumber()
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
        })

        alertDialog.show()
    }
}