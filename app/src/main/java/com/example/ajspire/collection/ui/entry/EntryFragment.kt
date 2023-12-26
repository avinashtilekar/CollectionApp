package com.example.ajspire.collection.ui.entry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.databinding.FragmentEntryBinding
import com.example.ajspire.collection.room.entity.TransactionTable

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
        binding.btnSubmit.setOnClickListener {
            entryViewModel.insert(TransactionTable(fee_type = "Sample", amount ="20", mobile_tran_key = "sjdksjdkj" ))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}