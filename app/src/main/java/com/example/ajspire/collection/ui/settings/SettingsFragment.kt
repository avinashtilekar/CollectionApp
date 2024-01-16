package com.example.ajspire.collection.ui.settings

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.ajspire.collection.BuildConfig
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.R
import com.example.ajspire.collection.api.helper.NetworkResult
import com.example.ajspire.collection.api.model.request.DataSyncRequest
import com.example.ajspire.collection.api.model.request.TransactionDataForUpload
import com.example.ajspire.collection.api.model.response.DataSyncResponse
import com.example.ajspire.collection.databinding.FragmentSettingsBinding
import com.example.ajspire.collection.extensions.appDataStore
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.ui.BaseFragment
import com.example.ajspire.collection.ui.custom.RadioGridGroup
import com.example.ajspire.collection.ui.dailog.ToastMessageUtility
import com.example.ajspire.collection.utility.AppUtility
import com.example.ajspire.collection.utility.PrinterType
import com.example.ajspire.collection.utility.ToastTypeFields
import com.example.ajspire.collection.view_model.ApiCallViewModel
import com.example.ajspire.collection.view_model.DataStoreViewModel
import com.example.ajspire.collection.view_model.DataStoreViewModelFactory
import com.example.ajspire.collection.view_model.EntryViewModelFactory
import com.example.ajspire.collection.view_model.RoomDataBaseViewModel
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files


class SettingsFragment : BaseFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var settingsViewModel: SettingsViewModel
    private val apiCallViewModel: ApiCallViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var currentSyncRecord = listOf<TransactionTable>()
    private lateinit var roomDBViewModel: RoomDataBaseViewModel
    val dataStoreViewModel: DataStoreViewModel by viewModels {
        DataStoreViewModelFactory(activity?.application!!, activity?.appDataStore()!!)
    }
    private var isSyncRecordHit: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomDBViewModel =
            ViewModelProvider(
                this,
                EntryViewModelFactory(
                    (activity?.application as MyApplication).repository,
                    (activity?.application as MyApplication)
                )
            ).get(
                RoomDataBaseViewModel::class.java
            )
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
        roomDBViewModel.allUnSyncTransactions.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Log.d("record found for upload", "record found for upload")
                currentSyncRecord = listOf()
                currentSyncRecord = it
                syncRecord()
            } else {
                Log.d("record not found for upload", "record not found for upload")
                roomDBViewModel.deleteSyncItems()

                if (isSyncRecordHit != null && isSyncRecordHit == true) {
                    toastMessageUtility.showToastMessage(getString(R.string.syn_sucess))
                }
                binding.llMainContaint.visibility = View.VISIBLE
                binding.llLoadding.visibility = View.GONE
                //binding.btnSync.visibility = View.GONE
                //binding.btnBackupData.visibility = View.GONE
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
                        roomDBViewModel.getAllUnSyncTransactions(AppUtility.UPLOAD_ITEM_LIMIT)
                    }


                }

                is NetworkResult.Error -> {
                    binding.llMainContaint.visibility = View.VISIBLE
                    binding.llLoadding.visibility = View.GONE
                    Log.d("Api", "Error")
                    toastMessageUtility.showToastMessage(
                        getString(R.string.technicale_error),
                        ToastTypeFields.Error
                    )
                    response.message?.let {
                        showErrorAlert("Api Response Error >>$it")
                    }
                }
            }
        }
    }


    private fun updateUi() {
        binding.txtHeading.text = getString(R.string.client_name)

        binding.txtFooter.text = getString(R.string.ajspire_tec)
        binding.txtBuild.text =
            "${BuildConfig.BUILD_DATE_TIME}\n" + BuildConfig.VERSION_NAME + (if (!BuildConfig.BUILD_TYPE_NAME.isNullOrBlank()) " " + BuildConfig.BUILD_TYPE_NAME else "")


        binding.btnSync.setOnClickListener {
            isSyncRecordHit = true
            roomDBViewModel.getAllUnSyncTransactions(AppUtility.UPLOAD_ITEM_LIMIT)
        }
        binding.btnBackupData.setOnClickListener {
            if (verifyStoragePermissions()) {
                isSyncRecordHit = false
                roomDBViewModel.getAllUnSyncTransactions(5000)
            }
        }
        if ((activity?.application as MyApplication).userPrinters == PrinterType.VriddhiDefault) {
            binding.rbDefaultPOSPrinter.isChecked = true
        } else if ((activity?.application as MyApplication).userPrinters == PrinterType.VriddhiExternal) {
            binding.rbExternalePrinter.isChecked = true
        }

        binding.rgPrinterType.setOnCheckedChangeListener(object :
            RadioGridGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGridGroup?, checkedId: Int) {
                AppUtility.hideSoftKeyboard(activity as Activity)
                var printerName = ""
                var userPrinterName: String? = null
                when (checkedId) {
                    R.id.rb_default_POS_printer -> {
                        printerName = getString(R.string.default_POS_printer)
                        (activity?.application as MyApplication).userPrinters =
                            PrinterType.VriddhiDefault
                        userPrinterName = PrinterType.VriddhiDefault.printerName
                    }

                    R.id.rb_externale_printer -> {
                        printerName = getString(R.string.externale_printer)
                        (activity?.application as MyApplication).userPrinters =
                            PrinterType.VriddhiExternal
                        userPrinterName = PrinterType.VriddhiExternal.printerName
                    }
                }
                userPrinterName?.let { dataStoreViewModel.updateUserPrinter(userPrinterName) }

                toastMessageUtility.showToastMessage(printerName + " " + getString(R.string.printers_selected_sucessfuly))
                view?.let {
                    Navigation.findNavController(it).navigate(R.id.action_setting_to_entry)
                }
            }

        })
    }

    private fun syncRecord() {
        try {

            val transactionDataForUploadList = arrayListOf<TransactionDataForUpload>()

            currentSyncRecord.forEach {
                transactionDataForUploadList.add(
                    TransactionDataForUpload(
                        it.amount,
                        it.customer_mobile_number,
                        it.customer_name,
                        it.fee_type,
                        it.mobile_tran_key,
                        it.invoice_number,
                        it.createdAt,
                        it.reprint
                    )
                )
            }
            if (transactionDataForUploadList.isNotEmpty()) {
                (activity?.application as MyApplication).loginUserDetails?.let { loginResponse ->
                    val dataSyncRequest =
                        DataSyncRequest(
                            transactionDataForUploadList,
                            loginResponse.user.id.toString()
                        )
                    if (isSyncRecordHit != null && isSyncRecordHit == true) {
                        //Hit sync record api
                        apiCallViewModel.dataSync(dataSyncRequest, loginResponse.token)
                    } else {
                        //Create backup file
                        createTextFileBackUp(Gson().toJson(dataSyncRequest))
                    }
                } ?: {
                    Log.d("User details not found", "User details not found please relogin")
                }

            }

        } catch (ex: Exception) {
            ex.message?.let {
                showErrorAlert("Sync Record Data Creation Error>>$it")
            }
        }
    }

    private fun updateUploadedRecord(dataSyncResponse: List<DataSyncResponse>) {
        currentSyncRecord.let {
            it.forEach { updateTransactionRecord ->
                updateTransactionRecord.server_tran_id =
                    getServerKey(dataSyncResponse, updateTransactionRecord.mobile_tran_key)
                roomDBViewModel
            }
        }

        roomDBViewModel.updateList(currentSyncRecord)
    }

    private fun getServerKey(
        dataSyncResponse: List<DataSyncResponse>,
        mobileTranKey: String
    ): String? {
        dataSyncResponse.forEach {
            if (it.mobile_tran_key == mobileTranKey) {
                return it.server_tran_id.toString()
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        roomDBViewModel.destroyViewModelData()
    }

    fun createBackup() {
        activity?.let { activity ->
            (activity.application as MyApplication).appDataBase.closeDatabase()
            val dbfile: File = activity.getDatabasePath(AppUtility.ROOM_DB_NAME)
            val sdir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                getString(R.string.app_name)
            )
            val sfpath =
                sdir.path + File.separator + AppUtility.BACKUP_ROOM_DB_NAME
            if (!sdir.exists()) {
                sdir.mkdirs()
            }
            val savefile = File(sfpath)
            try {
                savefile.createNewFile()
                val buffersize = 8 * 1024
                val buffer = ByteArray(buffersize)
                var bytes_read = buffersize
                val savedb: OutputStream = FileOutputStream(sfpath)
                val indb: InputStream = FileInputStream(dbfile)
                while (indb.read(buffer, 0, buffersize).also { bytes_read = it } > 0) {
                    savedb.write(buffer, 0, bytes_read)
                }
                savedb.flush()
                indb.close()
                savedb.close()
                toastMessageUtility.showToastMessage(
                    "Backup Created at " + savefile.absolutePath,
                    ToastTypeFields.Warning
                )
                //Need to reopen db after backup
                (activity.application as MyApplication).appDataBase.reOpenDataBase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createTextFileBackUp(jonsString: String) {
        activity?.let { activity ->

            val sdir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                getString(R.string.app_name)
            )
            val sfpath =
                sdir.path + File.separator + AppUtility.TXT_BACKUP_ROOM_DB_NAME
            if (!sdir.exists()) {
                sdir.mkdirs()
            }
            val savefile = File(sfpath)
            try {
                savefile.createNewFile()
                savefile.writeBytes(jonsString.toByteArray())
                toastMessageUtility.showToastMessage(
                    "Backup Created at " + savefile.absolutePath,
                    ToastTypeFields.Warning
                )
                //Need to reopen db after backup
                (activity.application as MyApplication).appDataBase.reOpenDataBase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}