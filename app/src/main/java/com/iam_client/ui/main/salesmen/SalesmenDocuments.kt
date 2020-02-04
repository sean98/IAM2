package com.iam_client.ui.main.salesmen


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.iam_client.R
import kotlinx.android.synthetic.main.fragment_salesmen_documents.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.iam_client.repostories.data.docs.*
import com.iam_client.ui.main.documents.DocumentView
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.main.salesman.SalesmanDocumentsViewModel
import com.iam_client.viewModels.main.salesman.SalesmanDocumentsViewModelFactory
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlin.reflect.KClass


class SalesmenDocuments : Fragment(), KodeinAware {
    override val kodein by closestKodein()

    private val viewModel  by lazy {
        val factory: SalesmanDocumentsViewModelFactory by instance()

        ViewModelProviders.of(this, factory)
            .get(SalesmanDocumentsViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_salesmen_documents, container, false)



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(viewModel.isAuthorized){
            val myAdapter =  ViewPagerFragmentAdapter(childFragmentManager,lifecycle)
            myAdapter.addFragment(DocumentList(viewModel.getQuotationsListViewModel()).apply {
                onRecordClicked = this@SalesmenDocuments.onRecordClicked
            })
            myAdapter.addFragment(DocumentList(viewModel.getOrdersListViewModel()).apply {
                onRecordClicked = this@SalesmenDocuments.onRecordClicked
            })
            myAdapter.addFragment(DocumentList(viewModel.getInvoicesListViewModel()).apply {
                onRecordClicked = this@SalesmenDocuments.onRecordClicked
            })
            view_pager.adapter = myAdapter
            TabLayoutMediator(tab_layout, view_pager, TabLayoutMediator.OnConfigureTabCallback { tab, position ->
                // Styling each tab here
                tab.text = when (position){
                    0->context!!.getString(R.string.quotations)
                    1->context!!.getString(R.string.orders)
                    2->context!!.getString(R.string.invoices)
                    else-> "ERROR - Not Implement"
                }
            }).attach()
        }
        else
            toast("error: no associated salesmen")
    }


    class ViewPagerFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
        :  FragmentStateAdapter(fragmentManager, lifecycle)
    {
        override fun createFragment(position: Int): Fragment {
           return getItem(position)
        }

        private val arrayList = arrayListOf<Fragment>()

        fun getItem(position: Int): Fragment {
            return arrayList[position]
        }

        fun addFragment(fragment: Fragment) {
            arrayList.add(fragment)
        }

        override fun getItemCount(): Int {
            return arrayList.size
        }
    }

    private val onRecordClicked :(record: Document)->Unit = { record->
        val type = when (record::class) {
            Invoice::class -> DocumentView.DocType.Invoice
            Order::class -> DocumentView.DocType.Order
            Quotation::class -> DocumentView.DocType.Quotation
            DeliveryNote::class -> DocumentView.DocType.DeliveryNote
            CreditNote::class -> DocumentView.DocType.CreditNote
            else -> null
        }
        findNavController().navigate(
            SalesmenDocumentsDirections.actionSalesmenDocumentsToDocumentView(
                record.sn!!,
                record.type
            )
        )

    }
}



