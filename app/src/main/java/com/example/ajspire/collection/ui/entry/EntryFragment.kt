package com.example.ajspire.collection.ui.entry

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.R
import com.example.ajspire.collection.databinding.FragmentEntryBinding
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
                    when(checkedId){
                        R.id.rb24 ->
                            etAmount.setText(R.string.fee_type_24_amt)
                        R.id.rb24X48 ->
                            etAmount.setText(R.string.fee_type_24_48_amt)
                        R.id.rb48X72 ->
                            etAmount.setText(R.string.fee_type_48_72_amt)
                        R.id.rb72X100 ->
                            etAmount.setText(R.string.fee_type_72_100_amt)
                    }
                }

            })
            btnSubmit.setOnClickListener {
                entryViewModel.insert(TransactionTable(fee_type = "Sample", amount ="20", mobile_tran_key = "sjdksjdkj" ))
            }
            btnCancel.setOnClickListener {
                etMobileNumber.setText("")
                etUserName.setText("")
                rb24.isChecked=true
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}