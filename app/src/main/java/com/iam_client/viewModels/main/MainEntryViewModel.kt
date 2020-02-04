package com.iam_client.viewModels.main

import androidx.lifecycle.*
import com.iam_client.repostories.data.Employee
import com.iam_client.repostories.employee.IEmployeeRepository
import com.iam_client.repostories.user.IUserRepository
import com.iam_client.utills.reactive.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainEntryViewModel(
    private val userRepository: IUserRepository,
    private val employeeRepository: IEmployeeRepository
) : ViewModel() {
    val errorMessage: LiveData<Event<Throwable>> = MutableLiveData()
    val loading = MutableLiveData<Boolean>()

    private val employee: LiveData<Employee?> =
        Transformations.switchMap(userRepository.getLoggedInUserLive()) { user ->
            val empSn = user?.employeeSN
            if (empSn != null) {
                refreshEmployee(empSn)
                employeeRepository.getCachedEmployee(user.employeeSN!!)
            } else
                MutableLiveData()
        }
    val profilePictureURL: LiveData<String?> = Transformations.map(employee) { it?.picUrl }


    private fun refreshEmployee(empSn: Int) = GlobalScope.launch {
        try {
            employeeRepository.refreshEmployee(empSn)
        } catch (error: Throwable) {
            (errorMessage as MutableLiveData).postValue(Event(error))
        }
    }

}


class MainEntryViewModelFactory(
    private val userRepository: IUserRepository,
    private val employeeRepository: IEmployeeRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainEntryViewModel(userRepository, employeeRepository) as T
    }
}