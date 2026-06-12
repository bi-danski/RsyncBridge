package org.me2you.rsyncbridge.core.crypto

import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoGCM {

    private const val KEY_ALIAS = "SIRI_GCM"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEYSTORE_TYPE = "PKCS12"
    private const val TAG_LENGTH = 128
    private const val IV_SIZE = 12
    private const val KEY_SIZE = 256

    private lateinit var keystoreFile: File
    private lateinit var keystorePassword: CharArray

    fun init(appDataDir: File, password: CharArray) {
        keystoreFile = File(appDataDir, "keystore.p12")
        keystorePassword = password
    }

    private val keyStore: KeyStore by lazy {
        check(::keystoreFile.isInitialized) { "Initialize CryptoGCM before use !!" }
        KeyStore.getInstance(KEYSTORE_TYPE).apply {
            if (keystoreFile.exists()) {
                keystoreFile.inputStream().use { load(it, keystorePassword) }
            } else load(null, keystorePassword)

        }
    }

    private fun persist() {
        keystoreFile.parentFile?.mkdirs()
        keystoreFile.outputStream().use { keyStore.store(it, keystorePassword) }
    }

    private fun getKey(): SecretKey {
        val entry = keyStore.getEntry(KEY_ALIAS, KeyStore.PasswordProtection(keystorePassword)) as? KeyStore.SecretKeyEntry
        return entry?.secretKey ?: generateKey()
    }

    private fun generateKey(): SecretKey {
        val key = KeyGenerator.getInstance("AES")
            .apply { init(KEY_SIZE) }
            .generateKey()
        keyStore.setEntry(
            KEY_ALIAS,
            KeyStore.SecretKeyEntry(key),
            KeyStore.PasswordProtection(keystorePassword)
        )
        persist()
        return key
    }

    fun deleteKey() {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.deleteEntry(KEY_ALIAS)
            persist()
        }
    }

    fun doEncrypt(bytes: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        return cipher.iv + cipher.doFinal(bytes)
    }

    fun doDecrypt(bytes: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getKey(),
            GCMParameterSpec(TAG_LENGTH, bytes.sliceArray(0 until IV_SIZE))
        )
        return cipher.doFinal(bytes.sliceArray(IV_SIZE until bytes.size))
    }
}