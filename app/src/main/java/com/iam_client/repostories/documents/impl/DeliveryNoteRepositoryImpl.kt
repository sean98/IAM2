package com.iam_client.repostories.documents.impl

import com.iam_client.local.dao.docs.DeliveryNoteDao
import com.iam_client.local.data.docs.DeliveryNoteEntity
import com.iam_client.remote.data.docs.DeliveryNoteDTO
import com.iam_client.remote.apiServices.docs.BaseDeliveryNoteService
import com.iam_client.remote.apiServices.docs.DeliveryNoteService
import com.iam_client.repostories.data.docs.DeliveryNote
import com.iam_client.repostories.documents.BasicDocumentRepository
import com.iam_client.repostories.utils.mappers.mapToEntity as toEntity
import com.iam_client.repostories.utils.mappers.mapToModel as toModel
import com.iam_client.repostories.utils.mappers.mapToDTO as toDTO


class DeliveryNoteRepositoryImpl private constructor(
    private val deliveryNoteDao: DeliveryNoteDao,
    private val deliveryNoteService: DeliveryNoteService
) : BasicDocumentRepository<DeliveryNote, DeliveryNoteEntity, DeliveryNoteDTO>(
    BaseDeliveryNoteService(deliveryNoteService), deliveryNoteDao
) {


    //Singleton factory
    companion object {
        @Volatile
        private var instance: DeliveryNoteRepositoryImpl? = null

        fun getInstance(deliveryNoteDao: DeliveryNoteDao, deliveryNoteService: DeliveryNoteService) =
            instance ?: synchronized(this) {
                instance ?: DeliveryNoteRepositoryImpl(deliveryNoteDao, deliveryNoteService)
                    .also { instance = it }
            }
    }

    override fun DeliveryNoteEntity.mapToModel(): DeliveryNote = toModel()
    override fun DeliveryNoteDTO.mapToEntity(): DeliveryNoteEntity = toEntity()
    override fun DeliveryNote.mapToDTO(): DeliveryNoteDTO = toDTO()

}