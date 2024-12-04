package com.gowittgroup.smartassistlib.util

import android.os.Build
import com.gowittgroup.smartassistlib.BuildConfig
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class KeyManager @Inject constructor() {

    fun generateAESKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    private fun generateRandomIV(): ByteArray {
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        return iv
    }


    fun generateSecretKey(): String {
        val secretKey = generateAESKey()
        return secretKeyToString(secretKey)
    }

    fun encrypt(data: String, secretKey: String) {
        val encryptedApiKeyData = encrypt(data, stringToSecretKey(secretKey))
        val encryptedApiKeyString = base64Encoder(encryptedApiKeyData.first)
        val encryptedIVString = base64Encoder(encryptedApiKeyData.second)

        println("ManageKey Encrypted API IV: $encryptedIVString")

        println("ManageKey Encrypted API Key: $encryptedApiKeyString")
    }

    fun getOriginalKey(encryptedKey: String, secretKey: String, iv: String): String {
        return decrypt(base64Decoder(encryptedKey), stringToSecretKey(secretKey), base64Decoder(iv))
    }

    fun encrypt(data: String, secretKey: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = generateRandomIV()
        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec)
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Pair(encryptedData, iv)
    }

    fun decrypt(encryptedData: ByteArray, secretKey: SecretKey, iv: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData)
    }


    fun secretKeyToString(secretKey: SecretKey): String {
        return secretKey.encoded.joinToString(separator = "") { "%02x".format(it) }
    }

    fun stringToSecretKey(secretKeyString: String): SecretKey {
        val keyBytes = ByteArray(32) { 0 }
        for (i in 0 until 32) {
            keyBytes[i] = secretKeyString.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return SecretKeySpec(keyBytes, "AES")
    }

    fun base64Encoder(byets: ByteArray): String {
        return if (Build.VERSION.SDK_INT >= 26) {
            Base64.getEncoder().encodeToString(byets)
        } else {
            android.util.Base64.encodeToString(byets, 0)
        }
    }

    fun base64Decoder(text: String): ByteArray {
        return if (Build.VERSION.SDK_INT >= 26) {
            Base64.getDecoder().decode(text)
        } else {
            android.util.Base64.decode(text, 0)
        }
    }

    fun getOpenAiKey() =
        getOriginalKey(BuildConfig.OPENAI_E_API_KEY, BuildConfig.SECRET, BuildConfig.OPENAI_VI)

    fun getGeminiKey() =
        getOriginalKey(BuildConfig.GEMINI_E_API_KEY, BuildConfig.SECRET, BuildConfig.GEMINI_VI)
}