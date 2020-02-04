//package com.iam_client.ui.main.documents
//
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.navigation.fragment.findNavController
//import androidx.paging.PagedList
//import androidx.recyclerview.widget.DefaultItemAnimator
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.iam_client.R
//import com.iam_client.repostories.data.Customer
//import com.iam_client.repostories.data.Salesman
//import com.iam_client.repostories.data.docs.*
//import com.iam_client.ui.adapters.DocumenHeadertListAdapter
//import com.iam_client.utills.reactive.observeEvent
//import com.iam_client.viewModels.main.documents.*
//import kotlinx.android.synthetic.main.fragment_documents_list.*
//import org.jetbrains.anko.support.v4.onRefresh
//import org.jetbrains.anko.support.v4.toast
//import org.kodein.di.KodeinAware
//import org.kodein.di.android.x.closestKodein
//import org.kodein.di.generic.instance
//import kotlin.reflect.KClass
//
//class DocumentsList< TDoc : Document> : Fragment(), KodeinAware {
//    override val kodein by closestKodein()
//    private lateinit var type: KClass<TDoc>
//    private lateinit var customer: Customer
//    private lateinit var salesman: Salesman
//    private val viewModel : IDocumentListViewModel<TDoc> by lazy {
//        val factory: DocumentListViewModelFactory<out Document>? = when (type) {
//            Invoice::class -> {
//                val f by instance<DocumentListViewModelFactory<Invoice>>(); f
//            }
//            Order::class -> {
//                val f by instance<DocumentListViewModelFactory<Order>>(); f
//            }
//            Quotation::class -> {
//                val f by instance<DocumentListViewModelFactory<Quotation>>(); f
//            }
//            DeliveryNote::class -> {
//                val f by instance<DocumentListViewModelFactory<DeliveryNote>>(); f
//            }
//            CreditNote::class -> {
//                val f by instance<DocumentListViewModelFactory<CreditNote>>(); f
//            }
//            else -> null
//        }
//
//        val vm = ViewModelProviders.of(this, factory!!)
//            .get(CustomerDocumentListViewModel::class.java)
//        vm.setCustomer(customer)
//
//        vm as IDocumentListViewModel<TDoc>
//    }
//
//    companion object {
//        fun <T : Document> newInstance(customer: Customer, type: KClass<T>): DocumentsList<T> {
//            val documentsList = DocumentsList<T>()
//            documentsList.customer = customer
//            documentsList.type = type
//            return documentsList
//        }
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_documents_list, container, false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        val adapter = DocumenHeadertListAdapter<TDoc>()
//        adapter.onItemClickListener = { _, r, _ -> onRecordClicked(r) }
//        doc_list.layoutManager = LinearLayoutManager(context)
//        doc_list.itemAnimator = DefaultItemAnimator()
//        doc_list.adapter = adapter
//
//        viewModel.getDocumentList().observe(this, Observer {
//            adapter.submitList(it as PagedList<TDoc>)
//            adapter.notifyDataSetChanged()
//        })
//
//        viewModel.refreshList()
//        refreshLayout.onRefresh { viewModel.refreshList() }
//        viewModel.refreshEvent.observeEvent(this) { refreshLayout.isRefreshing = it }
//        viewModel.errorMessage.observeEvent(this) { toast("error: ${it.message}") }
//    }
//
//
//    private fun onRecordClicked(record: TDoc) {
//        val type = when (type) {
//            Invoice::class -> DocumentView.DocType.Invoice
//            Order::class -> DocumentView.DocType.Order
//            Quotation::class -> DocumentView.DocType.Quotation
//            DeliveryNote::class -> DocumentView.DocType.DeliveryNote
//            CreditNote::class -> DocumentView.DocType.CreditNote
//            else -> null
//        }
//        findNavController().navigate(
//            DocumentTabbedFragmentDirections.actionDocumentFragmentToDocumentView(
//                record.sn!!,
//                type!!
//            )
//        )
//
//    }
//}
