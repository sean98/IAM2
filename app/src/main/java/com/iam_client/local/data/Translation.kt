package com.iam_client.local.data

import androidx.room.Entity
@Entity(tableName = "Translation",primaryKeys = ["source","languageCode"])
data class Translation(
    val source:String,
    val languageCode :String,
    val translatedText:String
)