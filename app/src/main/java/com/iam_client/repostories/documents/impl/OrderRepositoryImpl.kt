package com.iam_client.repostories.documents.impl

import com.iam_client.local.dao.docs.OrderDao
import com.iam_client.local.data.docs.OrderEntity
import com.iam_client.remote.data.docs.OrderDTO
import com.iam_client.remote.apiServices.docs.BaseOrderService
import com.iam_client.remote.apiServices.docs.OrderService
import com.iam_client.repostories.data.docs.Order
import com.iam_client.repostories.documents.BasicDocumentRepository
import com.iam_client.repostories.utils.mappers.mapToEntity as toEntity
import com.iam_client.repostories.utils.mappers.mapToModel as toModel
import com.iam_client.repostories.utils.mappers.mapToDTO as toDTO

class OrderRepositoryImpl private constructor(
    private val orderDao: OrderDao,
    private val orderService: OrderService
) : BasicDocumentRepository<Order, OrderEntity, OrderDTO>(BaseOrderService(orderService),orderDao){


    //Singleton factory
    companion object {
        @Volatile
        private var instance: OrderRepositoryImpl? = null
        fun getInstance(orderDao: OrderDao, orderService: OrderService) =
            instance ?: synchronized(this) {
                instance
                    ?: OrderRepositoryImpl(orderDao, orderService)
                        .also { instance = it }
            }
    }

    override fun OrderEntity.mapToModel(): Order = toModel()
    override fun OrderDTO.mapToEntity(): OrderEntity = toEntity()
    override fun Order.mapToDTO(): OrderDTO = toDTO()

}
