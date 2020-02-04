package com.iam_client.repostories.documents

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.iam_client.local.dao.docs.IDocumentDao
import com.iam_client.local.data.docs.DocumentEntity
import com.iam_client.remote.data.docs.DocumentDTO
import com.iam_client.remote.apiServices.docs.IDocumentService
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.data.Salesman
import com.iam_client.repostories.data.docs.Document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


abstract class BasicDocumentRepository<TDoc, TDocEntity, TDocDTO>
    (
    private val docWebService: IDocumentService<TDocDTO>,
    private val docDao: IDocumentDao<TDocEntity>
) : DocumentRepository<TDoc>
        where TDoc : Document, TDocEntity : DocumentEntity, TDocDTO : DocumentDTO {

    override fun getAllByCustomerWithPagination(customer: Customer): LiveData<PagedList<TDoc>> {
        val factory = docDao.getAllByCustomerSnPaged(customer.cid!!)
            .map { it.mapToModel() }

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(10) //.setPrefetchDistance(1)
            .build()

        return LivePagedListBuilder(factory, config)
            .build()
    }

    override fun getAllBySalesmanWithPagination(salesman: Salesman, openOnly: Boolean): LiveData<PagedList<TDoc>> {
        val factory = docDao.getAllBySalesmenCodePaged(salesman.sn,openOnly)
            .map { it.mapToModel() }

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(10) //.setPrefetchDistance(1)
            .build()

        return LivePagedListBuilder(factory, config)
            .build()    }

    override suspend fun refreshAllByCustomer(customer: Customer): List<TDoc> = withContext(Dispatchers.IO) {
        try {
            //get last update date of the cached documents
            val lastUpdateDate = docDao.getLastUpdatedDate(customerSN = customer.cid!!)
            //request documents that updated after the cache date
            val docsDTO = docWebService.getAll(cid = customer.cid, updatedAfter = lastUpdateDate)
            //cache the received documents
            val docEntity = docsDTO.map { it.mapToEntity() }
            docDao.upsertAll(docEntity)
            return@withContext docEntity.map { it.mapToModel() }
        } catch (error: Throwable) {
            throw error //TODO - Map error
        }
    }

    override suspend fun refreshAllBySalesmen(salesman: Salesman, openOnly: Boolean): List<TDoc> = withContext(Dispatchers.IO) {
        try {
            //get last update date of the cached documents
            val lastUpdateDate = docDao.getLastUpdatedDate(salesman.sn,openOnly)
            //request documents that updated after the cache date
            val docsDTO = docWebService.getAll(salesman.sn,openOnly, updatedAfter = lastUpdateDate)
            //cache the received documents
            val docEntity = docsDTO.map { it.mapToEntity() }
            docDao.upsertAll(docEntity)
            return@withContext docEntity.map { it.mapToModel() }
        } catch (error: Throwable) {
            throw error //TODO - Map error
        }
    }

    override fun getCachedBySn(sn: Int): LiveData<TDoc> {
        return Transformations.map(docDao.findBySN(sn)) { it?.mapToModel() }
    }

    override suspend fun addNew(document: TDoc): TDoc = withContext(Dispatchers.IO) {
        try {
            val addedDocument = docWebService.addNew(document.mapToDTO())
            val addedEntity = addedDocument.mapToEntity()
            docDao.upsert(addedEntity)
            return@withContext addedEntity.mapToModel()
        } catch (error: Throwable) {
            throw error //TODO - Map error
        }
    }

    override suspend fun update(document: TDoc): TDoc = withContext(Dispatchers.IO) {
        try {
            val updatedDocument = docWebService.update(document.mapToDTO())
            val updatedEntity = updatedDocument.mapToEntity()
            docDao.upsert(updatedEntity)
            return@withContext updatedEntity.mapToModel()
        } catch (error: Throwable) {
            throw error //TODO - Map error
        }
    }

    override suspend fun refreshBySn(sn: Int) = withContext(Dispatchers.IO) {
        //TODO exception handler
        //get document from web API
        val doc = docWebService.get(sn)

        //update the cache
        val docEntity = doc.mapToEntity()
        docDao.upsert(docEntity)
    }


    abstract fun TDocEntity.mapToModel(): TDoc

    abstract fun TDocDTO.mapToEntity(): TDocEntity

    abstract fun TDoc.mapToDTO(): TDocDTO


}
