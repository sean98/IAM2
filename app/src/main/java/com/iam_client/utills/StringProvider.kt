package com.iam_client.utills

import android.app.Application
import androidx.annotation.StringRes

class StringProvider(application: Application) {
    private val context = application.applicationContext
    fun getString(@StringRes id :Int):String =context.getString(id)
}