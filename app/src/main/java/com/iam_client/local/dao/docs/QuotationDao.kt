package com.iam_client.local.dao.docs
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.iam_client.local.data.docs.OrderEntity
import com.iam_client.local.data.docs.QuotationEntity

@Dao
abstract class QuotationDao  : IDocumentDao<QuotationEntity> {

    @Query("DELETE FROM Quotations")
    abstract override suspend fun deleteAll()

    @Query("SELECT * FROM Quotations WHERE sn = :sn")
    abstract override fun findBySN(sn:Int): LiveData<QuotationEntity?>


    @Query("SELECT * FROM Quotations WHERE customerSN = :customerSN ORDER BY date DESC, sn DESC")
    abstract override fun getAllByCustomerSnPaged(customerSN: String): DataSource.Factory<Int, QuotationEntity>


    @Query("SELECT MAX(creationDateTime) FROM Quotations WHERE customerSN = :customerSN")
    abstract override suspend fun getLastUpdatedDate(customerSN:String): Long?


    @Query("""SELECT MAX(creationDateTime) FROM Quotations 
      WHERE salesmanSN = :SalesmenCode AND
         ((:openOnly = 1 and isClosed = 0 and isCanceled = 0) OR (:openOnly = 0))""")
    abstract override suspend fun getLastUpdatedDate(SalesmenCode: Int, openOnly: Boolean): Long?

    @Query("""SELECT * FROM Quotations
         WHERE salesmanSN = :SalesmenCode AND
         ((:openOnly = 1 and isClosed = 0 and isCanceled = 0) OR (:openOnly = 0))
          ORDER BY date DESC, sn DESC""")
    abstract override fun getAllBySalesmenCodePaged(SalesmenCode: Int, openOnly: Boolean): DataSource.Factory<Int,QuotationEntity >

}