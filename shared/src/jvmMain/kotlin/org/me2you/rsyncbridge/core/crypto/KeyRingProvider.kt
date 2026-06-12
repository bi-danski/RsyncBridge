package org.me2you.rsyncbridge.core.crypto

import com.github.javakeyring.Keyring
import com.github.javakeyring.PasswordAccessException
import org.me2you.rsyncbridge.core.utils.logger
import java.security.SecureRandom
import java.util.Base64

object KeyRingProvider {

    private const val SERVICE = "rsync-bridge"
    private const val ACCOUNT = "datastore-keystore"

    fun getOrGeneratePasswd(): CharArray {
        Keyring.create().use { keyring ->
            return try {
                keyring.getPassword(SERVICE, ACCOUNT).toCharArray()
            } catch (ex: PasswordAccessException) {
                logger().warn { "KeyStoreProvider:: $ex.message"}
                val password = generatePassword()
                keyring.setPassword(SERVICE, ACCOUNT, String(password))
                password
            }
        }
    }

    private fun generatePassword(): CharArray {
        val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        return Base64.getEncoder().encodeToString(bytes).toCharArray()
    }
}