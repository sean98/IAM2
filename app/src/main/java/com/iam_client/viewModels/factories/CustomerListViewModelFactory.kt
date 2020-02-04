package com.iam_client.viewModels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iam_client.repostories.customer.CustomerRepository
import com.iam_client.viewModels.main.customers.CustomerListViewModel

class CustomerListViewModelFactory(private val customerRepository: CustomerRepository)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CustomerListViewModel(customerRepository) as T
    }
}