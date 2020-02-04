package com.iam_client.local.secure

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

class SecureStoring(private val context: Context) : ISecureStoring {
    //todo this class should be synchronized and singleton
    // to prevent multi-threading on same file

    override suspend fun write(key: String, value: ByteArray)  = withContext(Dispatchers.IO) {
        val (iv, encryptedValue) = Encryption.encrypt(value)
        ObjectOutputStream(context.openFileOutput(key, Context.MODE_PRIVATE)).use {
            it.writeObject(iv)
            it.writeObject(encryptedValue)
        }
    }

    override suspend fun read(key: String): ByteArray = withContext(Dispatchers.IO) {
        ObjectInputStream(context.openFileInput(key)).use {
            val iv = it.readObject() as ByteArray
            val encryptedData = it.readObject() as ByteArray
            return@withContext Encryption.decrypt(iv, encryptedData)
        }
    }
}