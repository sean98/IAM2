package com.iam_client.remote.apiServices.googleApi

import android.app.Application
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.iam_client.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface ITranslateService{
    suspend fun translateToLocal(text:String):String?
}

class TranslateService(application: Application):
    ITranslateService {
    private val context = application.applicationContext

    override suspend fun translateToLocal(text:String):String? = withContext(Dispatchers.IO){
        try {
            Log.i("Translator" ,"Translate $text ")
            context.resources.openRawResource(R.raw.transator8a4829ef5647).use {
                val myCredentials = GoogleCredentials.fromStream(it)
                val translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build()
                val translator = translateOptions.service
                val lang = Locale.getDefault().language
                val translation =translator.translate(text,
                    Translate.TranslateOption.targetLanguage(lang), Translate.TranslateOption.model("base"))
                return@withContext translation.translatedText
            }
        }catch (error:Throwable){
            Log.e("Translator" ,"Cant translate $text : ${error.message} ")
            return@withContext null
        }


    }
}