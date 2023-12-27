package com.example.ajspire.collection.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.BuildConfig
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.R
import com.example.ajspire.collection.api.helper.NetworkResult
import com.example.ajspire.collection.api.model.request.DataSyncRequest
import com.example.ajspire.collection.api.model.request.TransactionDataForUpload
import com.example.ajspire.collection.databinding.FragmentSettingsBinding
import com.example.ajspire.collection.extensions.appDataStore
import com.example.ajspire.collection.extensions.getLoginUserDetails
import com.example.ajspire.collection.extensions.userPreferencesDataStore
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.ui.dailog.ToastMessageUtility
import com.example.ajspire.collection.utility.AppUtility
import com.example.ajspire.collection.view_model.ApiCallViewModel
import com.example.ajspire.collection.view_model.DataBaseViewModel
import com.example.ajspire.collection.view_model.EntryViewModelFactory
import kotlinx.coroutines.flow.firstOrNull

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var settingsViewModel: SettingsViewModel
    private val apiCallViewModel: ApiCallViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var toastMessageUtility: ToastMessageUtility
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
        toastMessageUtility = ToastMessageUtility(requireActivity())
        setObserver()
        updateUi()
        return binding.root
    }

    private fun setObserver() {
        dataBaseViewModel.allUnSyncTransactions.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Log.d("record found for upload", "record found for upload")
                syncRecord(it)
            } else {
                Log.d("record not found for upload", "record not found for upload")
                toastMessageUtility.showToastMessage(getString(R.string.syn_sucess))
            }
        }
        apiCallViewModel.responseDataSyncResponse.observe(viewLifecycleOwner) { response ->
            when (response) {

                is NetworkResult.Loading -> {
                    Log.d("Api", "Loading")
                    AppUtility.hideSoftKeyboard(requireActivity())
                    binding.btnSync.visibility = View.GONE
                    binding.llLoadding.visibility = View.VISIBLE
                }

                is NetworkResult.Success -> {
                    //check once again for pending record
                    dataBaseViewModel.getAllUnSyncTransactions(AppUtility.UPLOAD_ITEM_LIMIT)
                }

                is NetworkResult.Error -> {
                    binding.btnSync.visibility = View.VISIBLE
                    binding.llLoadding.visibility = View.GONE
                    Log.d("Api", "Error")
                    toastMessageUtility.showToastMessage(getString(R.string.technicale_error), true)
                }
            }
        }
    }

    private fun updateUi() {
        binding.txtHeading.text = getString(R.string.client_name)
        binding.txtTitle.text = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME
        binding.txtFooter.text =
            "${BuildConfig.BUILD_DATE_TIME} \n\n${getString(R.string.ajspire_tec)}"

        binding.btnSync.setOnClickListener {
            dataBaseViewModel.getAllUnSyncTransactions(AppUtility.UPLOAD_ITEM_LIMIT)
        }
    }

    private fun syncRecord(list: List<TransactionTable>) {
        val transactionDataForUploadList = arrayListOf<TransactionDataForUpload>()

        list.forEach {
            transactionDataForUploadList.add(
                TransactionDataForUpload(
                    it.amount,
                    it.customer_mobile_number,
                    it.customer_name,
                    it.fee_type,
                    it.mobile_tran_key
                )
            )
        }
        if (transactionDataForUploadList.isNotEmpty()) {
            activity?.getLoginUserDetails()?.user?.id.toString().let { userId ->
                val dataSyncRequest = DataSyncRequest(transactionDataForUploadList, userId)
                apiCallViewModel.dataSync(dataSyncRequest)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}