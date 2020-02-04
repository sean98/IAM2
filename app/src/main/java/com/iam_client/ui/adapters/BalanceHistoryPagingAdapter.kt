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
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.data.CustomerBalanceRecord
import kotlinx.android.synthetic.main.finance_record.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import com.iam_client.ui.utils.Formatter.formatMoneyString

class BalanceHistoryPagingAdapter : PagedListAdapter<CustomerBalanceRecord,
        BalanceHistoryPagingAdapter.BalanceRecordVH>(BalanceRecordDiffs()) {

    var onItemClickListener: ((View, CustomerBalanceRecord, Long) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BalanceRecordVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.finance_record, parent, false)
        )

    override fun onBindViewHolder(holder: BalanceRecordVH, position: Int) {
        val record = getItem(position)
        holder.bindWith(record)
        if (record != null) {
            holder.itemView.onClick { v ->
                if (v != null && holder.adapterPosition!= RecyclerView.NO_POSITION)
                    onItemClickListener?.invoke(v, record, holder.adapterPosition.toLong())

            }
        }
    }


    class BalanceRecordVH(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        internal fun bindWith(record: CustomerBalanceRecord?) {
            if (record != null) {
                itemView.date_text.text = DateFormat.format("dd/MM/yy", record.date)
                itemView.ref1_text.text = record.doc1SN
                itemView.ref2_text.text = record.doc2SN
                itemView.debt_text.text = record.debt.formatMoneyString(record.currency)
                itemView.balance_text.text =record.balanceDebt.formatMoneyString(record.currency)
                itemView.type_text.text = when(record.type){
                    CustomerBalanceRecord.Type.Invoice -> itemView.context.getString(R.string.invoice)
                    CustomerBalanceRecord.Type.Receipt -> itemView.context.getString(R.string.receipt)
                    CustomerBalanceRecord.Type.CreditInvoice -> itemView.context.getString(R.string.credit_note)
                    CustomerBalanceRecord.Type.Journal -> itemView.context.getString(R.string.journal)
                    CustomerBalanceRecord.Type.Other -> itemView.context.getString(R.string.other)
                }
            }

        }
    }

    private class BalanceRecordDiffs : DiffUtil.ItemCallback<CustomerBalanceRecord>() {
        override fun areItemsTheSame(oldItem: CustomerBalanceRecord, newItem: CustomerBalanceRecord) =
            oldItem.sn == newItem.sn

        override fun areContentsTheSame(oldItem: CustomerBalanceRecord, newItem: CustomerBalanceRecord) =
            oldItem == newItem
    }
}