package org.me2you.rsyncbridge.core.crypto

import com.github.javakeyring.BackendNotSupportedException
import com.github.javakeyring.Keyring
import com.github.javakeyring.PasswordAccessException
import org.me2you.rsyncbridge.core.utils.logger
import java.io.File
import java.security.SecureRandom
import java.util.Base64

object KeyRingProvider {
    private const val SERVICE = "rsyncbridge"
    private const val ACCOUNT = "datastore-keystore"
    private val fallbackFile = File(System.getProperty("user.home"), ".rsyncbridge/._sync_")

    fun getOrGeneratePasswd(): CharArray {
        return try {
            useKeyring()
        } catch (_: BackendNotSupportedException) {
            logger().warn { "${this@KeyRingProvider} :: No keyring backend available, falling back." }
            initiateFileFallback()
        }
    }

    private fun useKeyring(): CharArray {
        Keyring.create().use { keyring ->
            return try {
                keyring.getPassword(SERVICE, ACCOUNT).toCharArray()
            } catch (ex: PasswordAccessException) {
                logger().warn { "${this@KeyRingProvider} :: ${ex.message}" }
                val password = generatePassword()
                keyring.setPassword(SERVICE, ACCOUNT, String(password))
                password
            }
        }
    }

    private fun initiateFileFallback(): CharArray {
        fallbackFile.parentFile.mkdirs()
        return if (fallbackFile.exists()) fallbackFile.readText().toCharArray()
            else {
                val password = generatePassword()
                fallbackFile.writeText(String(password))
                fallbackFile.setReadable(false, false)
                fallbackFile.setReadable(true, true)
                fallbackFile.setWritable(false, false)
                password
            }
    }

    private fun generatePassword(): CharArray {
        return Base64
            .getEncoder()
            .encodeToString(ByteArray(32).also {
                SecureRandom().nextBytes(it)
            } ).toCharArray()
    }
}