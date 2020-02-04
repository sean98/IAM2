package com.iam_client.ui.main.customers


import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.iam_client.R
import com.iam_client.databinding.FragmentCustomerListBinding
import com.iam_client.repostories.data.Customer
import com.iam_client.ui.adapters.CustomerAdapter
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.factories.CustomerListViewModelFactory
import com.iam_client.viewModels.main.customers.CustomerListViewModel
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import kotlinx.android.synthetic.main.fragment_customer_list.*
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class CustomerList : Fragment(), KodeinAware {

    override val kodein by closestKodein()
    private lateinit var binding: FragmentCustomerListBinding
    private val viewModel by lazy {
        val customerListViewModelFactory: CustomerListViewModelFactory by instance()

        ViewModelProviders
            .of(this, customerListViewModelFactory)
            .get(CustomerListViewModel::class.java)
    }


    private val options = navOptions {
        anim {
            enter = android.R.anim.slide_in_left
            exit = android.R.anim.slide_out_right
            popEnter = android.R.anim.slide_in_left
            popExit = android.R.anim.slide_out_right
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_customer_list, container, false
        )

        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        bindCustomerList()

        //handle refresh events
        refreshLayout.setOnRefreshListener { viewModel.refreshCustomers() }
        viewModel.refreshEvent.observeEvent(this) { refreshLayout.isRefreshing = it }


        viewModel.errorMessage.observeEvent(this){toast(it) }

        //on add new customer clicked -> navigate to new customer form
        viewModel.addCustomerEvent.observeEvent(viewLifecycleOwner) {
            findNavController().navigate(
                CustomerListDirections
                    .actionCustomerListToCustomerEditMode(Customer.getEmpty()),
                options
            )
        }

    }

    private fun bindCustomerList() {
        val customerAdapter = CustomerAdapter()
        customers_list.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        customers_list.layoutManager = LinearLayoutManager(context)
        customers_list.itemAnimator = DefaultItemAnimator()
        customers_list.adapter = SlideInBottomAnimationAdapter(customerAdapter)

        //create click listener foreach card, when a card as been clicked , navigate to CardView
        customerAdapter.onItemClickListener = { _, c, _ ->
            findNavController().navigate(
                CustomerListDirections
                    .actionCustomerListToCustomerInfo(c), options
            )
        }

        //observe changes at the customer page list an notify the UI
        viewModel.customers.observe(viewLifecycleOwner, Observer { pagedCustomers ->
            customerAdapter.submitList(pagedCustomers)
            customerAdapter.notifyDataSetChanged()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setQuery(viewModel.filteredText.value,false)
        //expand the search view if any query in use
        if(!viewModel.filteredText.value.isNullOrBlank()){
            searchView.setIconifiedByDefault(true)
            searchView.isFocusable = true
            searchView.isIconified = false
            searchView.requestFocusFromTouch()
            //for android.support.v7.widget.SearchView use only:
            //searchView.isIconified = false
            //searchView.clearFocus()
        }

        //on search query changed, notify the ViewModel
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                viewModel.filterCustomers(text ?: "")
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                viewModel.filterCustomers(text ?: "")
                return true
            }

        })
    }

}


//Some shit for prediction https://stackoverflow.com/a/22432635/4213730
//        activity?.also {
//            val searchManager = it.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(it.componentName))
//        }
