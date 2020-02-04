package com.iam_client.local.dao.docs

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iam_client.local.data.docs.OrderEntity

@Dao
abstract class OrderDao  : IDocumentDao<OrderEntity>  {


    @Query("DELETE FROM Orders")
    abstract override suspend fun deleteAll()

    @Query("SELECT * FROM Orders WHERE sn = :sn")
    abstract override fun findBySN(sn:Int): LiveData<OrderEntity?>

    @Query("SELECT * FROM Orders WHERE customerSN = :customerSN ORDER BY date DESC, sn DESC")
    abstract override fun getAllByCustomerSnPaged(customerSN: String): DataSource.Factory<Int, OrderEntity>


    @Query("SELECT MAX(creationDateTime) FROM Orders WHERE customerSN = :customerSN")
    abstract override suspend fun getLastUpdatedDate(customerSN:String): Long?

    @Query("""SELECT MAX(creationDateTime) FROM Orders 
      WHERE salesmanSN = :SalesmenCode AND
         ((:openOnly = 1 and isClosed = 0 and isCanceled = 0) OR (:openOnly = 0))""")
    abstract override suspend fun getLastUpdatedDate(SalesmenCode: Int, openOnly: Boolean): Long?

    @Query("""SELECT * FROM Orders
         WHERE salesmanSN = :SalesmenCode AND
         ((:openOnly = 1 and isClosed = 0 and isCanceled = 0) OR (:openOnly = 0))
          ORDER BY date DESC, sn DESC""")
    abstract override fun getAllBySalesmenCodePaged(SalesmenCode: Int, openOnly: Boolean): DataSource.Factory<Int, OrderEntity>
}