package com.iam_client.local.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SALESMAN")
data class SalesmanEntity (
    @PrimaryKey(autoGenerate = false)
    val sn : Int,
    val name : String?,
    val mobile : String? ,
    val email : String?,
    val isActive : Boolean
)