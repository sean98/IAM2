package com.iam_client.local.dao.docs

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iam_client.local.data.docs.CreditNoteEntity

@Dao
abstract class CreditNoteDao : IDocumentDao<CreditNoteEntity> {

    @Query("DELETE FROM CreditNotes")
    abstract override suspend fun deleteAll()

    @Query("SELECT * FROM CreditNotes WHERE sn = :sn")
    abstract override fun findBySN(sn:Int): LiveData<CreditNoteEntity?>

    @Query("SELECT * FROM CreditNotes WHERE customerSN = :customerSN  ORDER BY date DESC, sn DESC")
    abstract override fun getAllByCustomerSnPaged(customerSN: String): DataSource.Factory<Int, CreditNoteEntity>


    @Query("SELECT MAX(creationDateTime) FROM CreditNotes WHERE customerSN = :customerSN")
    abstract override suspend fun getLastUpdatedDate(customerSN:String): Long?


    @Query("""SELECT MAX(creationDateTime) FROM CreditNotes 
      WHERE salesmanSN = :SalesmenCode AND
         ((:openOnly = 1 and isClosed = 0 and isCanceled = 0) OR (:openOnly = 0))""")
    abstract override suspend fun getLastUpdatedDate(SalesmenCode: Int, openOnly: Boolean): Long?

    @Query("""SELECT * FROM CreditNotes 
         WHERE salesmanSN = :SalesmenCode AND
         ((:openOnly = 1 and isClosed = 0 and isCanceled = 0) OR (:openOnly = 0))
          ORDER BY date DESC, sn DESC""")
    abstract override fun getAllBySalesmenCodePaged(SalesmenCode: Int, openOnly: Boolean): DataSource.Factory<Int, CreditNoteEntity>


}