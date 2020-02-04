package com.iam_client.repostories.utils.settings

interface SettingsProvider {
    val useDevelopmentMode:Boolean

    val clearCacheOnStart : Boolean

    val useCustomURL:Boolean

    val serverURL :String?
}