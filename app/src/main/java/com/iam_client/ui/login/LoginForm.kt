package com.iam_client.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.iam_client.R

import com.iam_client.databinding.LoginFormFragmentBinding
import com.iam_client.ui.main.MainActivity
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.login.LoginFormViewModel
import com.iam_client.viewModels.login.LoginFormViewModelFactory
import org.jetbrains.anko.support.v4.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class LoginForm : Fragment(), KodeinAware {

    override val kodein by closestKodein()
    private val loginFormViewModelFactory: LoginFormViewModelFactory by instance()
    private lateinit var binding: LoginFormFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.login_form_fragment, container, false
        )

        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders
            .of(this, loginFormViewModelFactory)
            .get(LoginFormViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.loginEvent.observeEvent(this) {
            //TODO move to single activity
            //findNavController().navigate(LoginFormDirections.actionLoginFormToNavGraph())
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
            toast("Hello ${it.role}")

        }

        viewModel.errorMessage.observeEvent(this) {
            toast("error: ${it.message}")
        }
    }
}
