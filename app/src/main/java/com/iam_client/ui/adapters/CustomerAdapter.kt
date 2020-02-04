package com.iam_client.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iam_client.R
import com.iam_client.repostories.data.Customer
import kotlinx.android.synthetic.main.customer_header_record.view.*
import org.jetbrains.anko.image
import org.jetbrains.anko.sdk27.coroutines.onClick

class CustomerAdapter : PagedListAdapter<Customer,
        CustomerAdapter.CustomerVH>(CustomersDiffs()) {

    var onItemClickListener: ((View, Customer, Long) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CustomerVH(LayoutInflater.from(parent.context)
            .inflate(R.layout.customer_header_record, parent, false))



    override fun onBindViewHolder(holder: CustomerVH, position: Int) {
        val customer = getItem(position)
        holder.bindWith(customer)
        if (customer!=null) {
            holder.itemView.onClick { v ->
                if (v != null)
                    onItemClickListener?.invoke(v, customer, holder.adapterPosition.toLong())
            }
        }
        else {
            holder.itemView.onClick {  }
        }
    }


    class CustomerVH(view : View) : RecyclerView.ViewHolder(view) {
        internal fun bindWith(customer: Customer?) {
            itemView.name_title_text.text = customer?.name ?: ""
            itemView.group_text.text = customer?.group?.name ?: ""
            itemView.city_text.text = (customer?.billingAddress?.city
                ?: customer?.shippingAddress?.city) ?: ""

            itemView.type_image.visibility = View.VISIBLE
            when (customer?.type) {
                Customer.Type.Private -> itemView.type_image.image =
                    ContextCompat.getDrawable(itemView.context, R.drawable.home_city)
                Customer.Type.Company -> itemView.type_image.image =
                    ContextCompat.getDrawable(itemView.context, R.drawable.store)

                else -> itemView.type_image.visibility = View.GONE
            }
            if (customer != null) {
                itemView.placeholder.visibility = View.GONE
                itemView.shimmer_layout.stopShimmerAnimation()
            } else {
                itemView.placeholder.visibility = View.VISIBLE
                itemView.shimmer_layout.startShimmerAnimation()
            }
        }
    }

    private class CustomersDiffs : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer)
                = oldItem.cid == newItem.cid
        override fun areContentsTheSame(oldItem: Customer, newItem: Customer)
                = oldItem == newItem
    }
}