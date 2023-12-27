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
import com.example.ajspire.collection.api.helper.NetworkResult
import com.example.ajspire.collection.api.model.request.DataSyncRequest
import com.example.ajspire.collection.api.model.request.TransactionDataForUpload
import com.example.ajspire.collection.api.model.response.DataSyncResponse
import com.example.ajspire.collection.databinding.FragmentSettingsBinding
import com.example.ajspire.collection.extensions.getLoginUserDetails
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.ui.dailog.ToastMessageUtility
import com.example.ajspire.collection.utility.AppUtility
import com.example.ajspire.collection.view_model.ApiCallViewModel
import com.example.ajspire.collection.view_model.DataBaseViewModel
import com.example.ajspire.collection.view_model.EntryViewModelFactory

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

    private lateinit var currentSyncRecord:List<TransactionTable>

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
                currentSyncRecord=it
                syncRecord()
            } else {
                Log.d("record not found for upload", "record not found for upload")
                toastMessageUtility.showToastMessage(getString(R.string.syn_sucess))
                binding.llMainContaint.visibility = View.VISIBLE
                binding.llLoadding.visibility = View.GONE
                binding.btnSync.isEnabled=false
            }
        }
        apiCallViewModel.responseDataSyncResponse.observe(viewLifecycleOwner) { response ->
            when (response) {

                is NetworkResult.Loading -> {
                    Log.d("Api", "Loading")
                    AppUtility.hideSoftKeyboard(requireActivity())
                    binding.llMainContaint.visibility = View.GONE
                    binding.llLoadding.visibility = View.VISIBLE
                }

                is NetworkResult.Success -> {
                    response.data?.let {
                        //Update uploaded record server id
                        updateUploadedRecord(response.data)
                        //check once again for pending record
                        dataBaseViewModel.getAllUnSyncTransactions(AppUtility.UPLOAD_ITEM_LIMIT)
                    }


                }

                is NetworkResult.Error -> {
                    binding.llMainContaint.visibility = View.VISIBLE
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

    private fun syncRecord() {
        val transactionDataForUploadList = arrayListOf<TransactionDataForUpload>()

        currentSyncRecord.forEach {
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

    private fun updateUploadedRecord(dataSyncResponse: DataSyncResponse) {
        currentSyncRecord.let {
            it.forEach { updateTransactionRecord ->
                updateTransactionRecord.server_tran_id =getServerKey(dataSyncResponse,updateTransactionRecord.mobile_tran_key)
                dataBaseViewModel
            }
        }

        dataBaseViewModel.updateList(currentSyncRecord)
    }

    private fun getServerKey(dataSyncResponse: DataSyncResponse, mobileTranKey: String): String? {
        dataSyncResponse.data.forEach {
            if (it.mobile_tran_key.equals(mobileTranKey)) {
                return it.server_tran_id.toString()
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}