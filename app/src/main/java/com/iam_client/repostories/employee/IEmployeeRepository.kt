package com.iam_client.repostories.employee

import androidx.lifecycle.LiveData
import com.iam_client.remote.data.EmployeeDTO
import com.iam_client.repostories.data.Employee
import com.iam_client.repostories.utils.RepositoryCall

interface IEmployeeRepository {

    fun getAllActiveEmployees(): RepositoryCall<List<EmployeeDTO>>

    fun getCachedEmployee(sn: Int): LiveData<Employee?>

    suspend fun refreshEmployee(employeeSN: Int)

    suspend fun getEmployee(employeeSN:Int) :Employee
}
