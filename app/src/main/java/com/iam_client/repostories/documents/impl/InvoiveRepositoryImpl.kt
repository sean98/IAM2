package com.iam_client.repostories.documents.impl


import com.iam_client.local.dao.docs.InvoiceDao
import com.iam_client.local.data.docs.InvoiceEntity
import com.iam_client.remote.data.docs.InvoiceDTO
import com.iam_client.remote.apiServices.docs.BaseInvoiceService
import com.iam_client.remote.apiServices.docs.InvoiceService
import com.iam_client.repostories.data.docs.Invoice
import com.iam_client.repostories.documents.BasicDocumentRepository
import com.iam_client.repostories.utils.mappers.mapToEntity as toEntity
import com.iam_client.repostories.utils.mappers.mapToModel as toModel
import com.iam_client.repostories.utils.mappers.mapToDTO as toDTO

class InvoiceRepositoryImpl private constructor(
private val invoiceDao: InvoiceDao,
private val invoiceService: InvoiceService
) : BasicDocumentRepository<Invoice, InvoiceEntity, InvoiceDTO>(BaseInvoiceService(invoiceService),invoiceDao) {







    //Singleton factory
    companion object {
        @Volatile
        private var instance: InvoiceRepositoryImpl? = null
        fun getInstance(invoiceDao: InvoiceDao, invoiceService: InvoiceService) =
            instance ?: synchronized(this) {
                instance
                    ?: InvoiceRepositoryImpl(invoiceDao, invoiceService)
                        .also { instance = it }
            }
    }

    override fun InvoiceEntity.mapToModel(): Invoice = toModel()
    override fun InvoiceDTO.mapToEntity(): InvoiceEntity = toEntity()
    override fun Invoice.mapToDTO(): InvoiceDTO = toDTO()

}