package org.me2you.rsyncbridge.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.me2you.rsyncbridge.core.utils.BinUtil
import org.me2you.rsyncbridge.datastore.PreferenceRepo
import org.me2you.rsyncbridge.logger.logger
import java.io.File
import java.util.concurrent.TimeUnit

class IProxyService(
    private val koinScope: CoroutineScope,
    private val syncPrefs: PreferenceRepo
) {
    private var process: Process? = null
    private var serviceJob: Job? = null
    private val shutdownHook = Thread { stopSync() }

    init {
        Runtime
            .getRuntime()
            .addShutdownHook(shutdownHook)
    }

    fun isiProxyConnected(): Boolean = process?.isAlive == true

    fun startIProxy() {
        if (isiProxyConnected()) {
            logger().warn { "iProxy process already running" }
            return
        }
        serviceJob = koinScope.launch(Dispatchers.IO) {
            try {
                val pb = ProcessBuilder(buildIProxyCmd()).redirectErrorStream(true)
                process = pb.start()
                process?.inputStream?.bufferedReader()?.useLines { lines ->
                    lines.forEach { logger().info { "$it." } }
                }
            } catch (ex: Exception) {
                logger().error("iProxy failed 2 start", ex)
            }
        }
    }

    fun stopIProxy() {
        serviceJob?.cancel()
        stopSync()
    }

    fun monitorIProxyConnection() = callbackFlow {
        val currentProcess = process
        if (currentProcess == null || !currentProcess.isAlive) {
            trySend(false)
            close()
            return@callbackFlow
        }

        trySend(true)
        currentProcess.onExit().thenAccept {
            trySend(false)
            close()
        }

        awaitClose { logger().imetii { "iProxy monitor disposed." } }
    }

    private fun stopSync() {
        process?.let {
            if (it.isAlive) {
                logger().imetii { "Terminating iProxy process..." }
                it.destroyForcibly()
                it.waitFor(500, TimeUnit.MILLISECONDS)
            }
        }
        process = null
    }

    private fun buildIProxyCmd(): List<String> {
        return listOf(getIProxyBinPath()) +
                syncPrefs.iProxyConfig.value.args +
                syncPrefs.iProxyConfig.value.localPort.toString() +
                syncPrefs.iProxyConfig.value.remotePort.toString()
    }

    private fun getIProxyBinPath(): String {
        val osName = System.getProperty("os.name").lowercase()
        val osArch = System.getProperty("os.arch").lowercase()

        val (platform, arch) = when {
            osName.contains("win") -> { "windows" to BinUtil.getWindowsArch(osArch) }
            osName.contains("mac") -> { "macos" to BinUtil.getMacArch(osArch) }
            osName.contains("linux") -> { "linux" to BinUtil.getLinuxArch(osArch) }
            else ->
                throw UnsupportedOperationException("Unsupported operating system: $osName")
        }

        val binaryName = if (osName.contains("win")) "iproxy.exe" else "iproxy"
        val binaryPath = BinUtil.resolveBinaryPath(platform, arch, binaryName)

        require(File(binaryPath).exists() && File(binaryPath).isFile) {
            "iProxy binary not found at: $binaryPath\n"
        }

        if (!osName.contains("win")) {
            try {
                File(binaryPath).setExecutable(true)
            } catch (ex: Exception) {
                logger().warn { "Failed to set executable permissions: ${ex.message}" }
            }
        }

        logger().info { "Executing iProxy Binary: $binaryPath" }
        return binaryPath
    }
}