package org.me2you.rsyncbridge.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.me2you.rsyncbridge.sync.IProxyService
import org.me2you.rsyncbridge.sync.SSHService
import kotlin.time.Duration.Companion.milliseconds

class RootViewModel(
    private val iProxyService: IProxyService,
    private val sshService: SSHService
) : ViewModel() {

    private val _isSshConnected = MutableStateFlow(false)
    val isSshConnected: StateFlow<Boolean> = _isSshConnected.asStateFlow()

    private val _isProxyConnected = MutableStateFlow(false)
    val isProxyConnected: StateFlow<Boolean> = _isProxyConnected.asStateFlow()

    private var sshMonitorJob: Job? = null
    private var proxyMonitorJob: Job? = null

    fun pairMyIOS() {
        viewModelScope.launch(Dispatchers.IO) {
            sshService.triggerInit()
            iProxyService.startIproxy()
            delay(350.milliseconds)

            val connected = sshService.connect()
            if (!connected) {
                iProxyService.stopIproxy()
                return@launch
            }
            monitorConnection()
        }
    }

    fun unPairMyIOS() {
        viewModelScope.launch(Dispatchers.IO) {
            sshMonitorJob?.cancel()
            proxyMonitorJob?.cancel()
            sshService.disconnect()
            iProxyService.stopIproxy()
            _isSshConnected.value = false
            _isProxyConnected.value = false
        }
    }

    private fun monitorConnection() {
        sshMonitorJob?.cancel()
        proxyMonitorJob?.cancel()

        proxyMonitorJob = iProxyService.monitorConnection()
            .onEach { _isProxyConnected.value = it }
            .launchIn(viewModelScope)

        sshMonitorJob = sshService.monitorConnection()
            .onEach { _isSshConnected.value = it }
            .launchIn(viewModelScope)
    }
}