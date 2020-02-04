package com.iam_client.viewModels.login

import androidx.databinding.Bindable
import androidx.lifecycle.*
import com.iam_client.viewModels.utils.ObservableViewModel
import com.iam_client.utills.reactive.Event
import com.iam_client.repostories.data.user.User
import com.iam_client.repostories.user.IUserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginFormViewModel(
    private val userRepository: IUserRepository
) : ObservableViewModel() {

    @Bindable
    val username = MutableLiveData<String>()
    @Bindable
    val password = MutableLiveData<String>()
    @Bindable
    val loading = MutableLiveData<Boolean>()

    val loginEvent: LiveData<Event<User>> = MutableLiveData()

    val errorMessage: LiveData<Event<Throwable>> = MutableLiveData()

    init {
        loading.value = true
        runBlocking {
            try {
                val user = userRepository.getLoggedInUser()
                if(user!=null){
                    //a user is logged-in
                    (loginEvent as MutableLiveData).value = Event(user)
                }

            } catch (error: Throwable) {
                (errorMessage as MutableLiveData).value = Event(error)

            } finally {
                loading.value = false
            }
        }
    }

    fun login() = viewModelScope.launch {
        loading.value = true
        try {
            val user = userRepository.login(username.value.toString(), password.value.toString())
            (loginEvent as MutableLiveData).value = Event(user)
        } catch (error: Throwable) {
            (errorMessage as MutableLiveData).value = Event(error)

        } finally {
            loading.value = false
        }

    }


}

class LoginFormViewModelFactory(private val userRepository: IUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginFormViewModel(userRepository) as T
    }
}
