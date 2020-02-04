package com.iam_client.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.iam_client.local.data.CardGroupEntity
import com.iam_client.local.data.CustomerBalanceRecordEntity
import com.iam_client.local.data.CustomerEntity
import com.iam_client.repostories.data.CardGroup

@Dao
abstract  class CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(customer: CustomerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertAll(customers: List<CustomerEntity>)

    @Query("SELECT MAX(CASE WHEN lastUpdateDateTime = null then creationDateTime Else lastUpdateDateTime END) FROM CUSTOMERS")
    abstract suspend fun getLastUpdated(): Long?


    @Query("""SELECT * FROM CUSTOMERS
              ORDER BY name""")
    abstract fun getCustomersPaged(): DataSource.Factory<Int, CustomerEntity>

    @Query("""SELECT CUSTOMERS.*
         FROM CUSTOMERS
         JOIN CUSTOMERFTS ON (CUSTOMERS.cid == CUSTOMERFTS.cid) 
         WHERE CUSTOMERFTS MATCH '*' || :filter || '*' 
         ORDER BY CUSTOMERS.name""")
    abstract fun getCustomersPaged(filter: String): DataSource.Factory<Int, CustomerEntity>






    @Query("""SELECT CASE WHEN EXISTS(SELECT * FROM CUSTOMERS WHERE cid =:cid ) 
        THEN CAST (1 AS BIT) 
        ELSE CAST (0 as BIT) END""")
    abstract fun isCustomerExistByCid(cid:String) : Boolean


    @Query("""SELECT * FROM CUSTOMERS WHERE cid =:cid""")
    abstract fun getCustomerByCid(cid:String) : LiveData<CustomerEntity?>

    @Query("DELETE FROM CUSTOMERS")
    abstract suspend fun deleteAll()

    @Transaction
    open suspend fun rebuildCustomerData(customers: List<CustomerEntity>) {
        deleteAll()
        upsertAll(customers)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertAllCardGroups(customerGroup: List<CardGroupEntity>)

    @Query("SELECT * FROM CARDGROUPS")
    abstract fun getAllCardGroupsLive():LiveData<List<CardGroupEntity>>

    @Query("SELECT * FROM CARDGROUPS")
    abstract suspend fun getAllCardGroups():List<CardGroupEntity>

    @Query("SELECT * FROM CARDGROUPS WHERE sn = :groupCode ")
    abstract  fun getCardGroupLive(groupCode : Int):LiveData<CardGroupEntity>


    @Query("DELETE FROM CARDGROUPS")
    abstract suspend fun deleteAllCardGroups()

    @Transaction
    open suspend fun rebuildCardGroupData(groups: List<CardGroupEntity>) {
        deleteAllCardGroups()
        upsertAllCardGroups(groups)
    }






    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertAllBalanceRecords(customerBalanceRecords: List<CustomerBalanceRecordEntity>)

    @Query("SELECT Count(sn) FROM CUSTOMERBALANCERECORDS WHERE ownerSN =:cid ")
    abstract suspend fun countAllBalanceRecords(cid:String):Int

    @Query("""SELECT * FROM CUSTOMERBALANCERECORDS
        WHERE ownerSN =:cid 
        AND ((:nonZeroOnly = 1 AND balanceDebt!=0) OR(:nonZeroOnly = 0))
        ORDER BY date DESC, sn DESC""")
    abstract fun getAllBalanceRecordsPage(cid: String,nonZeroOnly : Boolean = false)
            : DataSource.Factory<Int, CustomerBalanceRecordEntity>

    @Query("SELECT SUM(balanceDebt) FROM CUSTOMERBALANCERECORDS WHERE ownerSN =:cid AND balanceDebt!=0")
    abstract suspend fun getTotalBalanceRecordsPage(cid: String): Double

}