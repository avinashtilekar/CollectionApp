package com.example.ajspire.collection.ui.entry

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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
import com.example.ajspire.collection.LoginActivity
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.R
import com.example.ajspire.collection.databinding.FragmentEntryBinding
import com.example.ajspire.collection.extensions.getLoginUserDetails
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.ui.custom.RadioGridGroup
import com.example.ajspire.collection.utility.AppUtility

class EntryFragment : Fragment() {

    private var _binding: FragmentEntryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val entryViewModel: EntryViewModel by viewModels {
        EntryViewModelFactory((activity?.application as MyApplication).repository)
    }
    private var selectedFeeType = "24"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        setObserver()
        updateUi()
        return binding.root
    }

    private fun setObserver() {
        entryViewModel.allTransactions.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("Data Found", it.toString())
            }
        })
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
                AppUtility.hideSoftKeyboard(requireActivity())
                entryViewModel.insert(
                    TransactionTable(
                        fee_type = selectedFeeType,
                        amount = etAmount.text.toString(),
                        mobile_tran_key = AppUtility.getMobileTranKey(activity?.getLoginUserDetails()?.user?.mobile_no),
                        customer_name = if (etUserName.text.toString()
                                .isNotEmpty()
                        ) etUserName.text.toString() else null,
                        customer_mobile_number = if (etMobileNumber.text.toString()
                                .isNotEmpty()
                        ) etMobileNumber.text.toString() else null
                    )
                )
                showConfirmAlert()
                reSetScreen()
            }
            btnCancel.setOnClickListener {
                reSetScreen()
            }
        }
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

    private fun showConfirmAlert() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        //set title for alert dialog
        builder.setTitle(R.string.menu_entry)
        //set message for alert dialog
        builder.setMessage(R.string.save_sucess_message)

        //performing positive action
        builder.setPositiveButton(R.string.close) { dialogInterface, which ->

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