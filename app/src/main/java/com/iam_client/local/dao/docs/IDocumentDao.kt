package com.iam_client.local.dao.docs

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iam_client.local.data.docs.DocumentEntity

@Dao
interface IDocumentDao <TDoc> where  TDoc : DocumentEntity{

     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun upsertAll(documents: List<TDoc>) //TODO - don't change allocated items (lazy loading API)

     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun upsert(document: TDoc)

     suspend fun deleteAll()

     fun findBySN(sn:Int): LiveData<TDoc?>

     fun getAllByCustomerSnPaged(customerSN: String): DataSource.Factory<Int, TDoc>

     fun getAllBySalesmenCodePaged(SalesmenCode: Int,openOnly:Boolean): DataSource.Factory<Int, TDoc>


     suspend fun getLastUpdatedDate(customerSN:String): Long?

     suspend fun getLastUpdatedDate(SalesmenCode: Int,openOnly:Boolean): Long?

}