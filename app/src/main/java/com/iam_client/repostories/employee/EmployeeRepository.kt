package com.iam_client.repostories.employee

import android.util.Log
import androidx.lifecycle.*
import com.iam_client.local.dao.EmployeeDao
import com.iam_client.remote.data.EmployeeDTO
import com.iam_client.remote.apiServices.EmployeeApiService
import com.iam_client.repostories.data.Employee
import com.iam_client.repostories.utils.RepositoryCall
import com.iam_client.repostories.utils.mappers.mapToEntity
import com.iam_client.repostories.utils.mappers.mapToModel
import kotlinx.coroutines.*

class EmployeeRepository private constructor(
    private val employeeApiService: EmployeeApiService,
    private val employeeDao: EmployeeDao


): IEmployeeRepository {


    private val allEmployees = MutableLiveData<List<EmployeeDTO>>()
    fun getAllEmployeesLive() = RepositoryCall<MutableLiveData<List<EmployeeDTO>>>(Dispatchers.IO){ call->
        if(call.isForcedRefresh){
            //load all employees from rest api (by pages of 100)
            var result = mutableListOf<EmployeeDTO>()
            do {
                //TODO handle exceptions (map to internal exceptions)
                val temp = employeeApiService.getEmployeesPaging(0, 100)
                result =  result.union(temp).toMutableList()
            }
            while (temp.size==100)
            //post loaded employees
            allEmployees.postValue(result)
            //return the shared LiveData - note that the data may update by another caller
            return@RepositoryCall allEmployees
            //TODO cache employees
        }
        return@RepositoryCall allEmployees
    }


    //TODO this is demo - not a real handler
    override fun getAllActiveEmployees() = RepositoryCall<List<EmployeeDTO>>(Dispatchers.IO) { call ->
        Log.i("getAllActiveEmployees", "  : ${Thread.currentThread().name} ")
        if (call.isForcedRefresh) {
            try {
                //TODO cache the result + map to project's internal class
                return@RepositoryCall employeeApiService.getEmployeesPaging(0, 100)
            } catch (error: Throwable) {
                //TODO handle (map) exceptions
                throw error
            }
        }
        TODO("no offline handler")
    }


    override fun getCachedEmployee(sn:Int) :LiveData<Employee?> =
        Transformations.map( employeeDao.findBySNLive(sn)){ it?.mapToModel() }

    override suspend fun getEmployee(employeeSN:Int) :Employee = withContext(Dispatchers.IO){
        var empEntity = employeeDao.findBySN(employeeSN)
        if(empEntity == null){
            refreshEmployee(employeeSN)
            empEntity = employeeDao.findBySN(employeeSN)
        }
        empEntity!!.mapToModel()
    }

    override suspend fun refreshEmployee(employeeSN:Int) = withContext(Dispatchers.IO){
        val empDTO = employeeApiService.getEmployee(employeeSN)
        employeeDao.upsert(empDTO.mapToEntity())
    }

    //newInstance singleton instance
    companion object {
        @Volatile private var instance: EmployeeRepository? = null
        fun getInstance(employeeApiApiService: EmployeeApiService,employeeDao: EmployeeDao) =
            instance ?: synchronized(this) {
                instance ?: EmployeeRepository(employeeApiApiService,employeeDao)
                    .also { instance=it }
            }
    }

}

