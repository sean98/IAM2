package com.iam_client.repostories.utils.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

const val CLEAR_DATA_ON_START = "CLEAR_DATA_ON_START"
const val USE_LOCAL_URL = "USE_LOCAL_URL"
const val DEVELOPMENT_SERVER_URL = "DEVELOPMENT_SERVER_URL"
const val DEPLOY_SERVER_URL = "DEPLOY_SERVER_URL"
const val USE_DEVELOPMENT_MODE = "USE_DEVELOPMENT_MODE"

class SettingsProviderImpl(context: Context) : SettingsProvider {


    private val appContext = context.applicationContext
    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    override val useCustomURL: Boolean
        get() = preferences.getBoolean(USE_LOCAL_URL, false) || !useDevelopmentMode

    override val clearCacheOnStart: Boolean
        get() = preferences.getBoolean(CLEAR_DATA_ON_START, false)

    override val serverURL: String?
        get() = if (useDevelopmentMode)
            preferences.getString(DEVELOPMENT_SERVER_URL, null)
        else preferences.getString(DEPLOY_SERVER_URL, null)

    override val useDevelopmentMode: Boolean
        get() = preferences.getBoolean(USE_DEVELOPMENT_MODE, true)


}