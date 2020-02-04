package com.iam_client.local.secure

interface ISecureStoring {

    suspend fun write(key: String, value: ByteArray)

    suspend fun read(key: String): ByteArray?
}