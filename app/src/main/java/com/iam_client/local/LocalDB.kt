package com.iam_client.local

import android.content.Context
import androidx.room.*
import com.iam_client.local.dao.CustomerDao
import com.iam_client.local.dao.EmployeeDao
import com.iam_client.local.dao.SalesmanDao
import com.iam_client.local.dao.TranslationDao
import com.iam_client.local.dao.docs.*
import com.iam_client.local.dao.products.ProductsDao
import com.iam_client.local.data.*
import com.iam_client.local.data.docs.*
import com.iam_client.local.data.products.ProductEntity
import com.iam_client.local.data.products.ProductCategoryEntity
import com.iam_client.local.utils.RoomConverters

@Database(entities = [
    Translation::class,
    CustomerEntity::class,
    CustomerFTS::class,
    CardGroupEntity::class,
    SalesmanEntity::class,
    EmployeeEntity::class,
    CustomerBalanceRecordEntity::class,
    InvoiceEntity::class,
    CreditNoteEntity::class,
    DeliveryNoteEntity::class,
    OrderEntity::class,
    QuotationEntity::class,
    ProductEntity::class,
    ProductCategoryEntity::class],
    version = 1)

@TypeConverters(RoomConverters::class)
abstract class LocalDB: RoomDatabase() {

    abstract fun translationDao(): TranslationDao
    abstract fun customerDao(): CustomerDao
    abstract fun employeeDao(): EmployeeDao

    abstract fun salesmanDao(): SalesmanDao
    //documents
    abstract fun quotationDao(): QuotationDao
    abstract fun orderDao(): OrderDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun deliveryNoteDao(): DeliveryNoteDao
    abstract fun creditNoteDao(): CreditNoteDao

    abstract fun productsDao(): ProductsDao

    companion object {
        @Volatile private var instance: LocalDB? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: buildDB(context).also { instance = it }
            }

        private fun buildDB(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                LocalDB::class.java, "IAM.db")
                .build()

    }
}