package com.example.ajspire.collection.ui.collection_list

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.PrinterCallBack
import com.example.ajspire.collection.R
import com.example.ajspire.collection.databinding.FragmentCollectionListBinding
import com.example.ajspire.collection.model.ItemModel
import com.example.ajspire.collection.ui.BaseFragment
import com.example.ajspire.collection.view_model.RoomDataBaseViewModel
import com.example.ajspire.collection.view_model.EntryViewModelFactory


class CollectionListFragment : BaseFragment(), PrinterCallBack {

    private var _binding: FragmentCollectionListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var listAdapter: ListAdapter

    private lateinit var roomDBViewModel: RoomDataBaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionListBinding.inflate(inflater, container, false)
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


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        updateUi()
    }

    private fun setObserver() {
        roomDBViewModel.allTransactions.observe(viewLifecycleOwner, Observer {
            it?.let {
                val list = mutableListOf<ItemModel>()
                it.map { tran ->
                    list.add(
                        ItemModel(
                            tran.id,
                            getString(R.string.fee_type) + " " + tran.fee_type,
                            getString(R.string.rs_sign) + " " + tran.amount,
                            tran.mobile_tran_key,
                            tran.invoice_number,
                            if (tran.customer_mobile_number != null) " " + getString(R.string.customer_mobile_number) + ": " + tran.customer_mobile_number else "",
                            if (tran.customer_name != null) " " + getString(R.string.customer_name) + ": " + tran.customer_name else "",
                            tran.server_tran_id,
                            tran.createdAt,
                            tran.updatedAt
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    binding.llSummarySection.visibility = View.VISIBLE
                    binding.rvList.visibility = View.VISIBLE
                    binding.tvNoCollectionError.visibility = View.GONE
                    showRecord(list)
                } else {
                    binding.llSummarySection.visibility = View.GONE
                    binding.rvList.visibility = View.GONE
                    binding.tvNoCollectionError.visibility = View.VISIBLE
                }
            }
        })

        roomDBViewModel.transactionSummary.observe(viewLifecycleOwner) {
            it?.let {
                var text: Spanned? = null
                text = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
                binding.tvSummary.text = text
            }
        }

        roomDBViewModel.transactionTableViaInvoiceNumber.observe(
            viewLifecycleOwner
        ) { transactionTable ->
            transactionTable?.let {
                currentTransactionTableInsert = it
                //for reprint callback
               // printReceipt(this, rePrint = true)
                printReceipt()
                currentTransactionTableInsert?.let {
                    roomDBViewModel.updateReprint(it.invoice_number)
                }
            }
        }

    }

    private fun updateUi() {
        binding.apply {
            rvList.layoutManager = LinearLayoutManager(requireContext())
            rvList.itemAnimator = DefaultItemAnimator()
        }
    }

    private fun showRecord(list: List<ItemModel>) {
        listAdapter = ListAdapter(
            list,
            getString(R.string.reciept_number) + " " + (activity?.application as MyApplication).invoiceNumberPrefix
        )
        listAdapter.apply {
            onRePrintClick = {
                roomDBViewModel.getTransactionViaInvoiceNumber(it)
            }
        }
        binding.rvList.adapter = listAdapter
    }

    override fun askForReprint() {
        showRePrintAlert()
    }

    override fun reRePrint() {
        currentTransactionTableInsert?.let {
            roomDBViewModel.updateReprint(it.invoice_number)
        }
        printReceipt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        roomDBViewModel.destroyViewModelData()
    }

}