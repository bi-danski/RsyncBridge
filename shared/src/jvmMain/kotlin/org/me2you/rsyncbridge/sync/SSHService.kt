package org.me2you.rsyncbridge.sync

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.me2you.rsyncbridge.core.datastore.PreferenceRepo
import org.me2you.rsyncbridge.core.utils.logger

class SSHService(
    private val sshClient: SSHClient,
    private val syncPrefs: PreferenceRepo
) {
    private val sshConfig
        get() = syncPrefs.sshConfig.value

    fun triggerInit() {
        try {
            sshClient.addHostKeyVerifier(PromiscuousVerifier())
//            sshClient.connection.keepAlive.keepAliveInterval = 5
        } catch (ex: Exception) { logger().error("SSH Init Error: ${ex.message}") }
    }

    fun connect() {
        try {
            sshClient.connect(sshConfig.host, sshConfig.port)
            sshClient.authPassword(sshConfig.user, sshConfig.pass)
        } catch (_: net.schmizz.sshj.userauth.UserAuthException) {
            logger().error("Invalid SSH Credentials. Check your SSH user and password configurations.")
            sshClient.disconnect()
        } catch (ex: Exception) {
            logger().error("SSH Connect Error: $ex")
            sshClient.disconnect()
        }
    }

    fun disconnect() {
        try {
            if (sshClient.isConnected) sshClient.disconnect()
        } catch (ex: Exception) { logger().error("SSH Disconnect Error: ${ex.message}") }
    }

    fun monitorConnection() = callbackFlow {
        try {
            if (!sshClient.isConnected) {
                trySend(false)
                close()
                return@callbackFlow
            }
            trySend(true)

            sshClient.transport.setDisconnectListener { reason, _ ->
                logger().info { "SSH Disconnect. Reason: $reason" }
                trySend(false)
                close()
            }

            awaitClose {
                sshClient.transport.disconnectListener = null
            }
        } catch (ex: Exception){ logger().error(ex.message ?: ex.localizedMessage) }
    }
}