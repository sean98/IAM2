package com.iam_client.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.iam_client.local.data.SalesmanEntity
@Dao
abstract class SalesmanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertAll(salesmen: List<SalesmanEntity>)

    @Query("SELECT * FROM SALESMAN")
    abstract fun getAllLive(): LiveData<List<SalesmanEntity>>

    @Query("SELECT * FROM SALESMAN")
    abstract suspend fun getAll(): Array<SalesmanEntity>

    @Query("SELECT * FROM SALESMAN WHERE sn = :sn")
    abstract fun findBySN(sn:Int): LiveData<SalesmanEntity>

    @Query("DELETE FROM SALESMAN")
    abstract suspend fun deleteAll()

    @Transaction
    open suspend fun rebuildData(salesmen: List<SalesmanEntity>) {
        deleteAll()
        upsertAll(salesmen)
    }
}