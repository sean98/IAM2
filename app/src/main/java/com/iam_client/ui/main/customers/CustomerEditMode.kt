package com.iam_client.ui.main.customers


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
import com.iam_client.databinding.FragmentCustomerEditModeBinding
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.utills.reactive.observeEventWithResult
import com.iam_client.viewModels.exceptions.ProceesAreadyRunningException
import com.iam_client.viewModels.exceptions.SomeFieldsNotValidException
import com.iam_client.viewModels.main.customers.CustomerCardEditModeModelFactory
import com.iam_client.viewModels.main.customers.CustomerEditViewModel
import com.iam_client.viewModels.main.customers.SetAddressSharedViewModel
import com.iam_client.viewModels.main.customers.SetAddressSharedViewModelFactory
import kotlinx.android.synthetic.main.fragment_customer_edit_mode.*
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CustomerEditMode : Fragment()  , KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentCustomerEditModeBinding
    private val viewModel by lazy { val modelFactory : CustomerCardEditModeModelFactory by instance()
        modelFactory.customer = navArgs<CustomerEditModeArgs>().value.customer
        ViewModelProviders
            .of(this,modelFactory)
            .get(CustomerEditViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_customer_edit_mode, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel



        viewModel.errorMessage.observeEvent(this){
            when(it){
                is SomeFieldsNotValidException -> toast(context!!.getString(R.string.error_some_field_not_valid))
                is  ProceesAreadyRunningException-> toast(context!!.getString(R.string.error_process_already_running_wait))
                else -> toast("Error: ${it.message}")
            }
        }

        viewModel.customerSavedEvent.observeEvent(viewLifecycleOwner){
            //TODO some success indicator
            if(navArgs<CustomerEditModeArgs>().value.customer.cid != null)
                this@CustomerEditMode.findNavController().navigateUp()//.popBackStack()
            else //new customer
                this@CustomerEditMode.findNavController().navigate(
                    CustomerEditModeDirections.actionCustomerEditModeToCustomerInfo(customer = it))
        }

        //show progress bar on saving
        viewModel.isSaving.observe(this, Observer { isSaving->
            if(isSaving)
                savingProgressBar.visibility = View.VISIBLE
            else
                savingProgressBar.visibility = View.GONE
        })


        //when address is clicked open SetAddressDialog and subscribe for the result
        viewModel.addressDialogNav.observeEventWithResult(viewLifecycleOwner) {
            //awaitAndGet shared ViewModel (shared with the activity) for sharing data between the fragment and the dialog
            val addressModelFactory : SetAddressSharedViewModelFactory by instance()
            val setAddressSharedViewModel = ViewModelProviders
                .of(activity!!,addressModelFactory)
                .get(SetAddressSharedViewModel::class.java)
            //init shared viewModel properties from the address
            setAddressSharedViewModel.initAddress(it)
            //open the dialog
            val addressDialog = CustomerEditModeDirections.actionCustomerEditModeToSetAddressDialog(it)
            findNavController().navigate(addressDialog)
            //subscribe the view model to dialog's result
            return@observeEventWithResult setAddressSharedViewModel.confirmedAddress
        }
    }
}





