package com.iam_client.local.secure

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

abstract class Encryption {
    //encryption based on https://www.raywenderlich.com/778533-encryption-tutorial-for-android-getting-started#toc-anchor-001

    companion object {

        fun initSecretKey() {
            if (getSecretKey() ==null) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                val keyGenParameterSpec = KeyGenParameterSpec.Builder("IAM-Key",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        //.setUserAuthenticationRequired(true) //requires lock screen, invalidated if lock screen is disabled
                        //.setUserAuthenticationValidityDurationSeconds(120) // 3 only available x seconds from password authentication. -1 requires finger print - every time
                        .setRandomizedEncryptionRequired(true) // 4 different ciphertext for same plaintext on each call
                        .build()
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }
        }

        fun encrypt(dataToEncrypt: ByteArray): Pair<ByteArray, ByteArray> {
            val secretKey = getSecretKey()

            //Encrypt data
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val ivBytes = cipher.iv
            val encryptedBytes = cipher.doFinal(dataToEncrypt)

            return Pair(ivBytes, encryptedBytes)
        }

        fun decrypt(iv: ByteArray, encrypted: ByteArray): ByteArray {
            val secretKey = getSecretKey()

            //Decrypt data
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            return cipher.doFinal(encrypted)
        }

        private fun getSecretKey(): SecretKey? {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val secretKeyEntry: KeyStore.Entry? =
                keyStore.getEntry("IAM-Key", null) ?: return null

            return (secretKeyEntry as KeyStore.SecretKeyEntry).secretKey
        }
    }
}