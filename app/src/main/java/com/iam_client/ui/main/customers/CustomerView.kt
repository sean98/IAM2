package com.iam_client.ui.main.customers


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.iam_client.R
import com.iam_client.databinding.FragmentCustomerViewBinding
import com.iam_client.ui.utils.Formatter.formatMoneyString
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.main.customers.CustomerCardViewModel
import com.iam_client.viewModels.main.customers.CustomerCardViewModelFactory
import kotlinx.android.synthetic.main.fragment_customer_view.*
import kotlinx.android.synthetic.main.fragment_customer_view.billing_address_text
import kotlinx.android.synthetic.main.fragment_customer_view.cell_text
import kotlinx.android.synthetic.main.fragment_customer_view.code_text
import kotlinx.android.synthetic.main.fragment_customer_view.comments_text
import kotlinx.android.synthetic.main.fragment_customer_view.email_text
import kotlinx.android.synthetic.main.fragment_customer_view.fax_text
import kotlinx.android.synthetic.main.fragment_customer_view.licTradNum_text
import kotlinx.android.synthetic.main.fragment_customer_view.name_text
import kotlinx.android.synthetic.main.fragment_customer_view.phone1_text
import kotlinx.android.synthetic.main.fragment_customer_view.phone2_text
import kotlinx.android.synthetic.main.fragment_customer_view.shipping_address_text
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class CustomerView : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentCustomerViewBinding
    private val viewModel by lazy {
        val customerCardViewModelFactory: CustomerCardViewModelFactory by instance()
        customerCardViewModelFactory.customerCid = navArgs<CustomerViewArgs>().value.customer.cid!!
        ViewModelProviders
            .of(this, customerCardViewModelFactory)
            .get(CustomerCardViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layut for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_customer_view, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.customerCardViewModel = viewModel


        //print customer
        viewModel.customer.observe(viewLifecycleOwner, Observer { customer ->
            customer?.apply {
                code_text.setText(cid)
                name_text.setText(name)
                licTradNum_text.setText(federalTaxID)
                phone1_text.setText(phone1)
                phone2_text.setText(phone2)
                cell_text.setText(cellular)
                fax_text.setText(fax)
                email_text.setText(email)
                balance_text.setText(balance.formatMoneyString(currency))
                billing_address_text.setText(billingAddress?.toStringFormat() ?: "")
                shipping_address_text.setText(shippingAddress?.toStringFormat() ?: "")
                billing_address_text.tag = billingAddress
                shipping_address_text.tag = shippingAddress
                //TODO handle (group + type)
                comments_text.setText(comments)
                salesman_text.setText(salesman.name)

            }

        })


        //init refresh layout
        refreshLayout.setOnRefreshListener { viewModel.refresh() }
        viewModel.refreshing.observe(viewLifecycleOwner, Observer { refreshLayout.isRefreshing = it.peekContent() })
        viewModel.refreshing.observeEvent(viewLifecycleOwner) { isRefreshing ->
            if (isRefreshing)
                toast("refreshing online")
        }


        //subscribe to error massages
        viewModel.errorMsg.observeEvent(viewLifecycleOwner) { errorMsg -> toast(errorMsg) }


        viewModel.updatedNotification.observeEvent(viewLifecycleOwner) { isUpdated ->
            if (isUpdated)
                toast("up to date")
        }


        viewModel.editCustomerNav.observeEvent(viewLifecycleOwner) {
            findNavController().navigate(
                CustomerViewDirections
                    .actionCustomerInfoToCustomerEditMode(it)
            )
        }

        viewModel.balanceHistoryNav.observeEvent(viewLifecycleOwner) {
            findNavController().navigate(
                CustomerViewDirections
                    .actionCustomerInfoToCustomerBalanceHistory(it)
            )
        }

        viewModel.documentsNav.observeEvent(this) {
            findNavController().navigate(
                CustomerViewDirections
                    .actionCustomerInfoToDocumentFragment(it))
        }

        viewModel.createDocumentsNav.observeEvent(this) {
            findNavController().navigate(
                CustomerViewDirections.actionCustomerInfoToDocumentEditMode(it))
        }
    }
}
