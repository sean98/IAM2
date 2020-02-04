package com.iam_client.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.iam_client.local.data.EmployeeEntity

@Dao
abstract class EmployeeDao {
    @Query("SELECT * FROM EMPLOYEES WHERE sn = :sn")
    abstract fun findBySNLive(sn:Int): LiveData<EmployeeEntity?>
    @Query("SELECT * FROM EMPLOYEES WHERE sn = :sn")
    abstract fun findBySN(sn:Int): EmployeeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(employee: EmployeeEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertAll(employees: List<EmployeeEntity>)

    @Query("DELETE FROM EMPLOYEES")
    abstract suspend fun deleteAll()


    @Transaction
    open suspend fun rebuildData(employees: List<EmployeeEntity>) {
        deleteAll()
        upsertAll(employees)
    }

}