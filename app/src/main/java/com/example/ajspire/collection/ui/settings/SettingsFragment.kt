package com.example.ajspire.collection.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.BuildConfig
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.R
import com.example.ajspire.collection.databinding.FragmentSettingsBinding
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.utility.AppUtility
import com.example.ajspire.collection.view_model.DataBaseViewModel
import com.example.ajspire.collection.view_model.EntryViewModelFactory

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var settingsViewModel: SettingsViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dataBaseViewModel: DataBaseViewModel by viewModels {
        EntryViewModelFactory((activity?.application as MyApplication).repository)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setObserver()
        updateUi()
        return binding.root
    }
    private fun setObserver() {
        dataBaseViewModel.allUnSyncTransactions.observe(viewLifecycleOwner) {
            it?.let {
                if(it.size>0)
                {

                }
            }
        }
    }
    private fun updateUi() {
        binding.txtHeading.text = getString(R.string.client_name)
        binding.txtTitle.text = getString(R.string.app_name)+ " "+BuildConfig.VERSION_NAME
        binding.txtFooter.text = "${BuildConfig.BUILD_DATE_TIME} \n\n${getString(R.string.ajspire_tec)}"

        binding.btnSync.setOnClickListener {
           dataBaseViewModel.getAllUnSyncTransactions(AppUtility.UPLOAD_ITEM_LIMIT)
        }
    }
    private fun syncRecord(list: List<TransactionTable>)
    {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}