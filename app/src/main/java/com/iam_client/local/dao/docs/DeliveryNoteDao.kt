package com.iam_client.local.dao.docs

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.iam_client.local.data.docs.CreditNoteEntity
import com.iam_client.local.data.docs.DeliveryNoteEntity

@Dao
abstract class DeliveryNoteDao : IDocumentDao<DeliveryNoteEntity> {

    @Query("DELETE FROM DeliveryNotes")
    abstract override suspend fun deleteAll()

    @Query("SELECT * FROM DeliveryNotes WHERE sn = :sn")
    abstract override fun findBySN(sn:Int): LiveData<DeliveryNoteEntity?>

    @Query("SELECT * FROM DeliveryNotes WHERE customerSN = :customerSN ORDER BY date DESC, sn DESC")
    abstract override fun getAllByCustomerSnPaged(customerSN: String): DataSource.Factory<Int, DeliveryNoteEntity>


    @Query("SELECT MAX(creationDateTime) FROM DeliveryNotes WHERE customerSN = :customerSN")
    abstract override suspend fun getLastUpdatedDate(customerSN:String): Long?

    @Query("""SELECT MAX(creationDateTime) FROM DeliveryNotes 
      WHERE salesmanSN = :SalesmenCode AND
         ((:openOnly = 1 and isClosed = 0 and isCanceled = 0) OR (:openOnly = 0))""")
    abstract override suspend fun getLastUpdatedDate(SalesmenCode: Int, openOnly: Boolean): Long?


    @Query("""SELECT * FROM DeliveryNotes 
         WHERE salesmanSN = :SalesmenCode AND
         ((:openOnly = 1 and isClosed = 0 and isCanceled = 0) OR (:openOnly = 0))
          ORDER BY date DESC, sn DESC""")
    abstract override fun getAllBySalesmenCodePaged(SalesmenCode: Int, openOnly: Boolean): DataSource.Factory<Int, DeliveryNoteEntity>

}