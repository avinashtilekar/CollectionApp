package com.example.ajspire.collection.ui.collection_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.databinding.CollectionListBinding
import com.example.ajspire.collection.ui.entry.EntryViewModel
import com.example.ajspire.collection.ui.entry.EntryViewModelFactory

class CollectionListFragment : Fragment() {

    private var _binding: CollectionListBinding? = null
    private val entryViewModel: EntryViewModel by viewModels {
        EntryViewModelFactory((activity?.application as MyApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CollectionListBinding.inflate(inflater, container, false)
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

    }
}