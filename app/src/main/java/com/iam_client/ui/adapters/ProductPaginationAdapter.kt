package com.iam_client.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.iam_client.R
import com.iam_client.repostories.data.products.Product
import com.iam_client.repostories.data.products.ProductCategory
import kotlinx.android.synthetic.main.product_view.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ProductPaginationAdapter : PagedListAdapter<Any, ProductPaginationAdapter.CatalogItemVH>(ProductDiffs()) {

    companion object {
        const val PRODUCT = 0
        const val CATEGORY = 1
        const val ANY = 2
    }

    var onClick : ((View, Any) -> Unit)? = null

    override fun getItemViewType(position: Int) =
        when (getItem(position)!!::class) {
            Product::class -> PRODUCT
            ProductCategory::class -> CATEGORY
            else -> ANY
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CatalogItemVH(LayoutInflater.from(parent.context).inflate(R.layout.product_view, parent, false))

    override fun onBindViewHolder(holder: CatalogItemVH, position: Int) {
        val item = getItem(position)
        when (getItemViewType(position)) {
            PRODUCT -> holder.bindWith(item as Product)
            CATEGORY -> holder.bindWith(item as ProductCategory)
            else -> return
        }
        holder.itemView.onClick { onClick?.invoke(it!!, item) }
    }

    class CatalogItemVH(view: View) : RecyclerView.ViewHolder(view) {
        fun bindWith(product: Product?) {
            product?.apply { bind(name, pictureURL) }
        }

        fun bindWith(category: ProductCategory?) {
            category?.apply { bind(name, pictureURL) }
        }

        private fun bind(name:String, url : String?) {
            itemView.product_name.text = name
            Glide.with(itemView)
                .load(url)
                .apply(RequestOptions()
                    .centerCrop()
                    .error(android.R.drawable.stat_notify_error)
                    .placeholder(CircularProgressDrawable(itemView.context).apply {
                        strokeWidth = 5f
                        centerRadius = 30f
                        start()
                    })
                )
                .into(itemView.product_image)
        }
    }

    class ProductDiffs : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any):Boolean {
            if (oldItem is Product && newItem is Product)
                return oldItem.code == newItem.code
            else if (oldItem is ProductCategory && newItem is ProductCategory)
                return oldItem.code == newItem.code
            return false
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any) : Boolean {
            if (oldItem is Product && newItem is Product)
                return oldItem == newItem
            else if (oldItem is ProductCategory && newItem is ProductCategory)
                return oldItem == newItem
            return false
        }
    }
}