package com.iam_client.repostories.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.iam_client.remote.apiServices.utils.IAuthorizationService
import com.iam_client.remote.apiServices.utils.UserDTO
import com.iam_client.repostories.data.user.User
import com.iam_client.repostories.employee.EmployeeRepository
import com.iam_client.repostories.employee.IEmployeeRepository
import kotlinx.coroutines.*

const val CLAIM_USERID_KEY = "userid"
const val CLAIM_ROLE_KEY = "role"
const val CLAIM_EMPLOYEECODE_KEY = "employeecode"


class UserRepository(
    private val authorizationService: IAuthorizationService
   // private val employeeRepository: IEmployeeRepository
) : IUserRepository {
    override suspend fun getLoggedInUser(): User? {
        if(loggedInUser.value ==null) {
            val user = authorizationService.getLoggedInUser()?.toUser()
            loggedInUser.postValue(user)
            return user
        }
        return loggedInUser.value
    }


    private var loggedInUser = MutableLiveData<User?>()
    override fun getLoggedInUserLive(): LiveData<User?> {
        return loggedInUser
    }

    override suspend fun login(username: String, password: String): User {
        //ask the api for an authorization token
        val userDTO = authorizationService.login(username, password)
        val user = userDTO.toUser()

//        val empSN = user.employeeSN
//        if(empSN!=null){
//            employeeRepository.refreshEmployee(empSN)
//        }
//
        loggedInUser.postValue(user)
        return user
    }

    override suspend fun logout() {
        loggedInUser.postValue(null)
        //TODO
        authorizationService.logout()
    }

    private fun UserDTO.toUser(): User {
        val userId = getTokenClaim<String>(CLAIM_USERID_KEY)!!
        val role = getTokenClaimEnum<User.Role>(CLAIM_ROLE_KEY)!!
        val employeeSN = getTokenClaim<String>(CLAIM_EMPLOYEECODE_KEY)
        return User(userId.toInt(), role).apply {
            this.employeeSN = employeeSN?.toInt()
        }
    }

    //newInstance singleton instance
    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(authorizationService: IAuthorizationService) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(authorizationService)
                    .also { instance = it }
            }
    }
}