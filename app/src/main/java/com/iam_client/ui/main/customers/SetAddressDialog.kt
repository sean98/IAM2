package com.iam_client.ui.main.customers

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.iam_client.R
import com.iam_client.databinding.FragmentSetAddressDialogBinding
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.exceptions.SomeFieldsNotValidException
import com.iam_client.viewModels.main.customers.SetAddressSharedViewModel
import com.iam_client.viewModels.main.customers.SetAddressSharedViewModelFactory
import kotlinx.android.synthetic.main.fragment_set_address_dialog.*
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class SetAddressDialog : DialogFragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentSetAddressDialogBinding
    private val viewModel by lazy {
        val modelFactory: SetAddressSharedViewModelFactory by instance()
        ViewModelProviders
            .of(activity!!, modelFactory)
            .get(SetAddressSharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //transparent background
        dialog?.window!!.setBackgroundDrawable(ColorDrawable())
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_set_address_dialog, container, false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //note that the address's properties gain and initialized by the view model (shared viewModel)
        binding.viewModel = viewModel

        //when address is confirmed (a valid address checked by view model)
        //dismiss the dialog
        viewModel.confirmedAddress.observe(this, Observer {
            //dismiss the fragment
            this@SetAddressDialog.dismiss()
        })


        viewModel.errorMessage.observeEvent(this){
            when(it){
                is SomeFieldsNotValidException -> toast(context!!.getString(R.string.error_some_field_not_valid))
                else -> toast("Error: ${it.message}")
            }
        }


        viewModel.errorFields.observe(this, Observer { errorList ->
            id_text.error = if(errorList.contains(SetAddressSharedViewModel.ErrorField.Name))
                context!!.getString(R.string.error_address_name_empty) else null

            city_text.error = if(errorList.contains(SetAddressSharedViewModel.ErrorField.City))
                context!!.getString(R.string.error_empty_field) else null
        })


    }
}
