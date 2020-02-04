package com.iam_client.repostories.documents.impl
import com.iam_client.local.dao.docs.QuotationDao
import com.iam_client.local.data.docs.QuotationEntity
import com.iam_client.remote.data.docs.QuotationDTO
import com.iam_client.remote.apiServices.docs.BaseQuotationService
import com.iam_client.remote.apiServices.docs.QuotationService
import com.iam_client.repostories.data.docs.Quotation
import com.iam_client.repostories.documents.BasicDocumentRepository
import com.iam_client.repostories.documents.IQuotationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.iam_client.repostories.utils.mappers.mapToEntity as toEntity
import com.iam_client.repostories.utils.mappers.mapToModel as toModel
import com.iam_client.repostories.utils.mappers.mapToDTO as toDTO

class QuotationRepository private constructor(
    private val quotationDao: QuotationDao,
    private val quotationService: QuotationService
) : BasicDocumentRepository<Quotation, QuotationEntity, QuotationDTO>(BaseQuotationService(quotationService),quotationDao) ,
    IQuotationRepository {
    override suspend fun cancelDoc(quotation: Quotation)  = withContext(Dispatchers.IO){
        val canceledDoc = quotationService.cancel(quotation.sn!!)
        quotationDao.upsert(canceledDoc.mapToEntity())
    }

    //Singleton factory
    companion object {
        @Volatile
        private var instance: QuotationRepository? = null
        fun getInstance(quotationDao: QuotationDao, quotationService: QuotationService) =
            instance ?: synchronized(this) {
                instance
                    ?: QuotationRepository(quotationDao, quotationService)
                    .also { instance = it }
            }
    }

    override fun QuotationEntity.mapToModel(): Quotation = toModel()
    override fun QuotationDTO.mapToEntity(): QuotationEntity = toEntity()
    override fun Quotation.mapToDTO(): QuotationDTO = toDTO()

}