package org.me2you.rsyncbridge.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.me2you.rsyncbridge.core.datastore.PreferenceRepo
import org.me2you.rsyncbridge.core.utils.logger
import org.me2you.rsyncbridge.data.ProxyConfig
import org.me2you.rsyncbridge.data.SshConfig


class ConfViewModel(private val syncPrefs: PreferenceRepo) : ViewModel() {

    var sshHost = mutableStateOf(syncPrefs.sshConfig.value.host)
    var sshPort = mutableStateOf(syncPrefs.sshConfig.value.port)
    var sshUser = mutableStateOf(syncPrefs.sshConfig.value.user)
    var sshPass = mutableStateOf(syncPrefs.sshConfig.value.pass)

    var proxyLocalPort = mutableStateOf(syncPrefs.iProxyConfig.value.localPort)
    var proxyRemotePort = mutableStateOf(syncPrefs.iProxyConfig.value.remotePort)
    var proxyArgs = mutableStateOf(syncPrefs.iProxyConfig.value.args)

    var argInput = mutableStateOf("")
    var passVisible = mutableStateOf(false)
    var savedVisible = mutableStateOf(false)

    init {
        viewModelScope.launch {
            syncPrefs.sshConfig.collect { prefs ->
                sshHost.value = prefs.host
                sshPort.value = prefs.port
                sshUser.value = prefs.user
                sshPass.value = prefs.pass
            }
        }
        viewModelScope.launch {
            syncPrefs.iProxyConfig.collect { config ->
                proxyLocalPort.value = config.localPort
                proxyRemotePort.value = config.remotePort
                proxyArgs.value = config.args
            }
        }
    }

    suspend fun onSaveConfigs(sshConfig: SshConfig, proxyConfig: ProxyConfig) {
        try {
            syncPrefs.updateSSHPreferences { sshConfig.toSSHPreferences() }
            syncPrefs.updateIproxyPreferences { proxyConfig.toIProxyPreferences() }
        } catch (ex: Exception) {
            logger().error(ex.message ?: ex.localizedMessage)
        }
    }

    suspend fun onResetConfigs() {
        try {
            syncPrefs.clearAllPreferences()

            logger().imetii { "Configurations Reset To Default." }
        } catch (ex: Exception) {
            logger().error(ex.message ?: ex.localizedMessage)
        }
    }
}