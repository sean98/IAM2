package com.iam_client.repostories.documents.impl
import com.iam_client.local.dao.docs.CreditNoteDao
import com.iam_client.local.data.docs.CreditNoteEntity
import com.iam_client.remote.data.docs.CreditNoteDTO
import com.iam_client.remote.apiServices.docs.BaseCreditNoteService
import com.iam_client.remote.apiServices.docs.CreditNoteService
import com.iam_client.repostories.data.docs.CreditNote
import com.iam_client.repostories.documents.BasicDocumentRepository
import com.iam_client.repostories.utils.mappers.mapToEntity as toEntity
import com.iam_client.repostories.utils.mappers.mapToModel as toModel
import com.iam_client.repostories.utils.mappers.mapToDTO as toDTO


class CreditNoteRepositoryImpl private constructor(
    private val creditNoteDao: CreditNoteDao,
    private val creditNoteService: CreditNoteService
) : BasicDocumentRepository<CreditNote, CreditNoteEntity, CreditNoteDTO>(BaseCreditNoteService(creditNoteService),creditNoteDao){



    //Singleton factory
    companion object {
        @Volatile
        private var instance: CreditNoteRepositoryImpl? = null
        fun getInstance(creditNoteDao: CreditNoteDao, creditNoteService: CreditNoteService) =
            instance ?: synchronized(this) {
                instance
                    ?: CreditNoteRepositoryImpl(creditNoteDao, creditNoteService)
                        .also { instance = it }
            }
    }


    override fun CreditNoteEntity.mapToModel(): CreditNote = toModel()
    override fun CreditNoteDTO.mapToEntity(): CreditNoteEntity =toEntity()
    override fun CreditNote.mapToDTO(): CreditNoteDTO = toDTO()
}