package com.iam_client.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.iam_client.R
import com.iam_client.repostories.data.products.Product
import com.iam_client.repostories.data.products.ProductCategory
import com.iam_client.ui.adapters.ProductPaginationAdapter
import com.iam_client.utills.GridSpacingItemDecoration
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.main.CatalogViewModel
import com.iam_client.viewModels.main.CatalogViewModelFactory
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.catalog_fragmnet.*
import kotlinx.android.synthetic.main.catalog_fragmnet.refreshLayout
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class CatalogList : DialogFragment(), KodeinAware {
    override val kodein by closestKodein()

    var onProductClicked: ((Product) -> Unit)? = null

    val viewModel by lazy {
        val factory: CatalogViewModelFactory by instance()
        ViewModelProviders.of(this, factory)
            .get(CatalogViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.catalog_fragmnet, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val numberOfColumns = 2
        val adapter = ProductPaginationAdapter()
        catalog_list.layoutManager = GridLayoutManager(context, numberOfColumns)
        catalog_list.adapter = ScaleInAnimationAdapter(adapter)
        catalog_list.addItemDecoration(GridSpacingItemDecoration(numberOfColumns, 50, true))

        val categoriesLiveData = viewModel.getCategories()

        categoriesLiveData.observe(this, object: Observer<PagedList<ProductCategory>> {
            override fun onChanged(it: PagedList<ProductCategory>?) {
                adapter.submitList(it as PagedList<Any>)
                adapter.notifyDataSetChanged()
                adapter.onClick = { _, item ->
                    categoriesLiveData.removeObserver(this)

                    viewModel.getProducts(category = item as ProductCategory)
                        .observe(this@CatalogList, Observer {
                            adapter.submitList(it as PagedList<Any>)
                            adapter.notifyDataSetChanged()
                            adapter.onClick = { _, item ->
                                onProductClicked?.invoke(item as Product)
                            }
                        })
                }
            }
        })

        viewModel.errorMessage.observeEvent(this){toast("error: ${it.message}") }
        refreshLayout.setOnRefreshListener { viewModel.refresh() }

        viewModel.refreshEvent.observeEvent(this) { refreshLayout.isRefreshing = it }

    }
}