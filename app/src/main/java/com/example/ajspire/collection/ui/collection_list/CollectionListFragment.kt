package com.example.ajspire.collection.ui.collection_list

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ajspire.collection.MyApplication
import com.example.ajspire.collection.R
import com.example.ajspire.collection.databinding.FragmentCollectionListBinding
import com.example.ajspire.collection.ui.BaseFragment
import com.example.ajspire.collection.ui.model.ItemModel
import com.example.ajspire.collection.view_model.DataBaseViewModel
import com.example.ajspire.collection.view_model.EntryViewModelFactory


class CollectionListFragment : BaseFragment() {

    private var _binding: FragmentCollectionListBinding? = null
    private val dataBaseViewModel: DataBaseViewModel by viewModels {
        EntryViewModelFactory((activity?.application as MyApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var listAdapter: ListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionListBinding.inflate(inflater, container, false)
        setObserver()
        updateUi()
        return binding.root
    }

    private fun setObserver() {
        dataBaseViewModel.allTransactions.observe(viewLifecycleOwner, Observer {
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
                    binding.llSummarySection.visibility=View.VISIBLE
                    binding.rvList.visibility=View.VISIBLE
                    binding.tvNoCollectionError.visibility=View.GONE
                    showRecord(list)
                }else{
                    binding.llSummarySection.visibility=View.GONE
                    binding.rvList.visibility=View.GONE
                    binding.tvNoCollectionError.visibility=View.VISIBLE
                }
            }
        })

        dataBaseViewModel.transactionSummary.observe(viewLifecycleOwner) {
            it?.let {
                var text: Spanned? = null
                text = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
                binding.tvSummary.text = text
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
        listAdapter = ListAdapter(list,getString(R.string.reciept_number)+" "+(activity?.application as MyApplication).invoiceNumberPrefix)
        listAdapter.apply {
            onRePrintClick={

            }
        }
        binding.rvList.adapter = listAdapter
    }
}