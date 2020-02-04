package com.iam_client.ui.adapters

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iam_client.R
import com.iam_client.repostories.data.docs.Document
import kotlinx.android.synthetic.main.document_header_record.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import com.iam_client.ui.utils.Formatter.formatMoneyString

class DocumenHeadertListAdapter< TDoc : Document>: PagedListAdapter<TDoc,
        DocumenHeadertListAdapter.DocumentRecordVH<TDoc>>(DocumentRecordDiffs()) {

    var onItemClickListener: ((View, TDoc, Long) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DocumentRecordVH<TDoc>(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.document_header_record, parent, false)
        )

    override fun onBindViewHolder(holder: DocumentRecordVH<TDoc>, position: Int) {
        val record = getItem(position)
        holder.bindWith(record)
        if (record != null) {
            holder.itemView.onClick { v ->
                if (v != null)
                    onItemClickListener?.invoke(v, record, holder.adapterPosition.toLong())

            }
        }
    }


    class DocumentRecordVH<TDoc : Document>(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        internal fun bindWith(record: TDoc?) {
            if (record != null) {
                itemView.dateTextView.text = DateFormat.format("dd/MM/yy", record.date)
                itemView.carNameTextView.text = record.customerName
                itemView.docSnTextView.text = record.sn.toString()
                itemView.docTotalTextView.text = record.docTotal?.formatMoneyString(record.currency)
                itemView.typeTextView.text = " "
                itemView.isCanceledIcon.visibility = if(record.isCanceled) View.VISIBLE else View.GONE
                itemView.isCloseIcon.visibility = if(record.isClosed) View.VISIBLE else View.GONE
            }

        }
    }

    private class DocumentRecordDiffs<TDoc : Document> : DiffUtil.ItemCallback<TDoc>() {
        override fun areItemsTheSame(oldItem: TDoc, newItem: TDoc) =
            oldItem.sn == newItem.sn

        override fun areContentsTheSame(oldItem: TDoc, newItem: TDoc) =
            oldItem.equals(newItem)
    }
}
