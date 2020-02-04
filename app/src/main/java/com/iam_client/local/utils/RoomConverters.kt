package com.iam_client.local.utils

import androidx.room.TypeConverter
import com.iam_client.local.data.CustomerBalanceRecordEntity
import com.iam_client.local.data.CustomerEntity
import com.iam_client.local.data.EmployeeEntity
import java.sql.Date
import java.util.Collections.emptyList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iam_client.local.data.CardGroupEntity
import com.iam_client.local.data.docs.DocItemEntity
import com.iam_client.local.data.products.ProductPropertyEntity
import com.iam_client.remote.data.products.ProductPropertyDTO
import com.iam_client.repostories.data.CardGroup


object RoomConverters {
    private val gson = Gson()

//    @TypeConverter
//    fun fromTimestamp(value: Long?): Date? {
//        return if (value == null) null else Date(value)
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: Date?): Long? {
//        return date?.time
//    }

    @TypeConverter
    @JvmStatic
    fun GendersToString(ds: EmployeeEntity.Genders?) = ds?.name

    @TypeConverter
    @JvmStatic
    fun sToGenders(s: String?) = s?.let(EmployeeEntity.Genders::valueOf)

    @TypeConverter
    @JvmStatic
    fun TypesToString(ds: CustomerEntity.Types?) = ds?.name

    @TypeConverter
    @JvmStatic
    fun sToMyTypes(s: String?) = s?.let(CustomerEntity.Types::valueOf)


    @TypeConverter
    @JvmStatic
    fun RecordTypesToString(ds: CustomerBalanceRecordEntity.Type?) = ds?.name

    @TypeConverter
    @JvmStatic
    fun sToMyRecordTypes(s: String?) = s?.let(CustomerBalanceRecordEntity.Type::valueOf)


    @TypeConverter
    @JvmStatic
    fun stringToList(data: String?): List<DocItemEntity>? {
        if (data == null) {
            return null
        }
        val listType = object : TypeToken<List<DocItemEntity>>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    @JvmStatic
    fun ListToString(someObjects: List<DocItemEntity>?): String? {
        if (someObjects == null) return null
        return gson.toJson(someObjects)
    }


    @TypeConverter
    @JvmStatic
    fun propertyTypesToString(ds: ProductPropertyEntity.PropertyType) = ds.name

    @TypeConverter
    @JvmStatic
    fun stringToMyPropertyType(s: String) = s.let(ProductPropertyEntity.PropertyType::valueOf)


    @TypeConverter
    @JvmStatic
    fun ProductPropertiesToJsonString(ds:List<ProductPropertyEntity>) = gson.toJson(ds)

    @TypeConverter
    @JvmStatic
    fun jsonStringToProductProperties(s: String?) :List<ProductPropertyEntity> {
        val mapType = object : TypeToken<List<ProductPropertyEntity>>(){}.type
        return gson.fromJson(s,mapType)
    }


}