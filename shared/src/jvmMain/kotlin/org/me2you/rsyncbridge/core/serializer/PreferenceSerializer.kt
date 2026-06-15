package org.me2you.rsyncbridge.core.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.me2you.rsyncbridge.core.crypto.CryptoGCM
import org.me2you.rsyncbridge.datastore.SyncPreferences
import org.me2you.rsyncbridge.logger.logger
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.UnrecoverableKeyException

object PreferenceSerializer : Serializer<SyncPreferences> {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true
    }

    override val defaultValue: SyncPreferences
        get() = SyncPreferences()

    override suspend fun readFrom(input: InputStream): SyncPreferences {
        return withContext(Dispatchers.IO) {
            val encryptedBytes = try {
                input.readBytes()
            } catch (e: IOException) {
                logger().error("$this@SyncSerializer :: Failed to read input stream", e)
                return@withContext defaultValue
            }
            if (encryptedBytes.isEmpty()) return@withContext defaultValue

            try {
                val decryptedBytes = CryptoGCM.doDecrypt(encryptedBytes)
                json.decodeFromString(
                    deserializer = SyncPreferences.serializer(),
                    string = decryptedBytes.decodeToString()
                )
            } catch (e: SerializationException) {
                logger().error("$this@SyncSerializer :: Data structure mismatch in SyncPreferences", e)
                throw CorruptionException("Failed to deserialize SyncPreferences", e)
            } catch (e: Exception) {
                logger().error("$this@SyncSerializer :: Decryption failed. Keystore might be out of sync.", e)
                if (isKeyInvalidated(e)) defaultValue
                    else throw IOException("Unrecoverable decryption error", e)
            }
        }
    }

    override suspend fun writeTo(t: SyncPreferences, output: OutputStream) {
        withContext(Dispatchers.IO) {
            try {
                val bytes = json.encodeToString(SyncPreferences.serializer(), t).toByteArray()

                val encryptedBytes = try {
                    CryptoGCM.doEncrypt(bytes)
                } catch (ex: Exception) {
                    if (isKeyInvalidated(ex)) {
                        logger().warn { "$this@SyncSerializer :: Key invalidated. Regenerating for new write." }
                        CryptoGCM.deleteKey()
                        CryptoGCM.doEncrypt(bytes)
                    } else throw ex
                }
                output.write(encryptedBytes)
            } catch (ex: Exception) {
                logger().error("$this@SyncSerializer :: Failed to write encrypted preferences", ex)
                throw IOException("Unable to safely encrypt or write SyncPreferences", ex)
            }
        }
    }

    private fun isKeyInvalidated(ex: Exception): Boolean {
        return ex is UnrecoverableKeyException ||
                ex.message?.contains("Key invalidated", ignoreCase = true) == true ||
                ex.message?.contains("User authentication", ignoreCase = true) == true ||
                ex.message?.contains("keystore", ignoreCase = true) == true
    }
}