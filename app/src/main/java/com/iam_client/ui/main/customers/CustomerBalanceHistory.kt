package com.iam_client.ui.main.customers


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import com.iam_client.R
import com.iam_client.databinding.FragmentCustomerBalanceHistoryBinding
import com.iam_client.repostories.data.CustomerBalanceRecord
import com.iam_client.repostories.data.docs.Document
import com.iam_client.ui.adapters.BalanceHistoryPagingAdapter
import com.iam_client.ui.main.documents.DocumentView
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.main.customers.BalanceHistoryViewModel
import com.iam_client.viewModels.main.customers.BalanceHistoryViewModelFactory
import kotlinx.android.synthetic.main.fragment_customer_balance_history.*
import kotlinx.android.synthetic.main.fragment_customer_balance_history.refreshLayout
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CustomerBalanceHistory : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentCustomerBalanceHistoryBinding

    private val viewModel by lazy {
        val factory: BalanceHistoryViewModelFactory by instance()
        factory.customer = navArgs<CustomerBalanceHistoryArgs>().value.customer
        ViewModelProviders
            .of(this, factory)
            .get(BalanceHistoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_customer_balance_history, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        //init record list recycle view
        val adapter = BalanceHistoryPagingAdapter()
        records_list.layoutManager = LinearLayoutManager(context)
        records_list.itemAnimator = DefaultItemAnimator()
        records_list.adapter = adapter
        adapter.onItemClickListener = { _, r, _ -> onRecordClicked(r) }

        //when record streaming origin change - reassign the adapter
        viewModel.records.observe(viewLifecycleOwner, Observer { records ->
            adapter.submitList(records)
            adapter.notifyDataSetChanged()
        })

        viewModel.refreshEvent.observeEvent(viewLifecycleOwner) { refreshLayout.isRefreshing = it }
        refreshLayout.setOnRefreshListener { viewModel.refreshRecords() }

        //show errors with toast
        viewModel.errorMsg.observeEvent(viewLifecycleOwner) { toast(it) }
    }

    private fun onRecordClicked(record: CustomerBalanceRecord) {
        //if the record is not null and contains a supported document , then navigate to DocumentView
        if (record.doc1SN != null && record.doc1SN.isDigitsOnly()) {
            val type = when (record.type){
                CustomerBalanceRecord.Type.Invoice->Document.Type.Invoice
                CustomerBalanceRecord.Type.CreditInvoice->Document.Type.CreditNote
                else -> null
            }
            if (type!=null) {
                findNavController().navigate(
                    CustomerBalanceHistoryDirections
                        .actionCustomerBalanceHistoryToDocumentView(record.doc1SN.toInt(), type)
                )
            } else {
                toast("Unsupported Document! TODO -string")//TODO const string
            }
        }
    }
}
