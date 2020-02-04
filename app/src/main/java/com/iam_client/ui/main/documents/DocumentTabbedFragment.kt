package com.iam_client.ui.main.documents


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator

import com.iam_client.R
import com.iam_client.repostories.data.docs.*
import com.iam_client.ui.adapters.CustomViewPagerAdapter
import com.iam_client.ui.main.salesmen.DocumentList
import com.iam_client.ui.main.salesmen.SalesmenDocumentsDirections
import com.iam_client.viewModels.main.documents.CustomerDocumentsTabsViewModel
import com.iam_client.viewModels.main.documents.CustomerDocumentsTabsViewModelFactory
import kotlinx.android.synthetic.main.document_tabbed_fragment.*
import kotlinx.android.synthetic.main.document_tabbed_fragment.tab_layout
import kotlinx.android.synthetic.main.document_tabbed_fragment.view_pager
import kotlinx.android.synthetic.main.fragment_salesmen_documents.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlin.reflect.KClass

class DocumentTabbedFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModel  by lazy {
        val factory: CustomerDocumentsTabsViewModelFactory by instance()
        factory.customer =  navArgs<DocumentTabbedFragmentArgs>().value.customer
        ViewModelProviders.of(this, factory)
            .get(CustomerDocumentsTabsViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.document_tabbed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val myAdapter =  ViewPagerFragmentAdapter(childFragmentManager,lifecycle)
        myAdapter.addFragment(DocumentList(viewModel.getQuotationsListViewModel()).apply {
            onRecordClicked = this@DocumentTabbedFragment.onRecordClicked
        })
        myAdapter.addFragment(DocumentList(viewModel.getOrdersListViewModel()).apply {
            onRecordClicked = this@DocumentTabbedFragment.onRecordClicked
        })
        myAdapter.addFragment(DocumentList(viewModel.getDeliveryNoteListViewModel()).apply {
            onRecordClicked = this@DocumentTabbedFragment.onRecordClicked
        })
        myAdapter.addFragment(DocumentList(viewModel.getInvoicesListViewModel()).apply {
            onRecordClicked = this@DocumentTabbedFragment.onRecordClicked
        })
        myAdapter.addFragment(DocumentList(viewModel.getCreditNotesListViewModel()).apply {
            onRecordClicked = this@DocumentTabbedFragment.onRecordClicked
        })
        view_pager.adapter = myAdapter

        TabLayoutMediator(tab_layout, view_pager, TabLayoutMediator.OnConfigureTabCallback { tab, position ->
            // Styling each tab here
            tab.text = when (position){
                0->context!!.getString(R.string.quotations)
                1->context!!.getString(R.string.orders)
                2->context!!.getString(R.string.delivery_notes)
                3->context!!.getString(R.string.invoices)
                4->context!!.getString(R.string.credit_notes)
                else-> "ERROR - Not Implement"
            }
        }).attach()
    }




    class ViewPagerFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
        :  FragmentStateAdapter(fragmentManager, lifecycle)
    {
        private val arrayList = arrayListOf<Fragment>()

        override fun createFragment(position: Int): Fragment {
            return getItem(position)
        }

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
        findNavController().navigate(
            DocumentTabbedFragmentDirections.actionDocumentFragmentToDocumentView(
                record.sn!!,
                record.type
            )
        )

    }
}
