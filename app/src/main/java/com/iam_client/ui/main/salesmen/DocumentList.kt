package com.iam_client.ui.main.salesmen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.iam_client.R
import com.iam_client.repostories.data.docs.*
import com.iam_client.ui.adapters.DocumenHeadertListAdapter
import com.iam_client.utills.reactive.observe
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.main.documents.IDocumentListViewModel
import kotlinx.android.synthetic.main.fragment_documents_list.*
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.toast

class DocumentList(val viewModel: IDocumentListViewModel) : Fragment(){
    var onRecordClicked : ((record: Document)-> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_documents_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = DocumenHeadertListAdapter<Document>()
        doc_list.layoutManager = LinearLayoutManager(context)
        doc_list.itemAnimator = DefaultItemAnimator()
        adapter.onItemClickListener = { _, r, _ -> onRecordClicked?.invoke(r) }
        doc_list.adapter = adapter

        viewModel.getDocumentList().observe(this) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        viewModel.refresh()
        refreshLayout.onRefresh { viewModel.refresh() }
        viewModel.refreshEvent.observeEvent(this) { refreshLayout.isRefreshing = it }
        viewModel.errorMessage.observeEvent(this) { toast("error: ${it.message}") }


    }




}


