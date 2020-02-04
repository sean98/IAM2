package com.iam_client.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iam_client.local.data.Translation

@Dao
abstract class TranslationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(translation: Translation)

    @Query(
        """SELECT * FROM Translation 
         WHERE languageCode = :languageCode 
         AND source in (:sources)"""
    )
    abstract fun getTranslationLive(
        sources: Array<String>,
        languageCode: String
    ): LiveData<Array<Translation>>


    @Query(
        """SELECT CASE WHEN EXISTS
        (SELECT * FROM Translation WHERE languageCode = :languageCode AND  source=:source) 
        THEN CAST (1 AS BIT) 
        ELSE CAST (0 as BIT) END"""
    )
    abstract suspend fun isTranslationExist(source: String, languageCode: String): Boolean

}