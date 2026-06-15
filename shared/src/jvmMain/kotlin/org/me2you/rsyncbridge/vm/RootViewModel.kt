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
import org.me2you.rsyncbridge.ui.state.UiState
import kotlin.time.Duration.Companion.milliseconds

class RootViewModel(
    private val iProxyService: IProxyService,
    private val sshService: SSHService
) : ViewModel() {

    private val _isSshConnected = MutableStateFlow(false)
    val isSshConnected: StateFlow<Boolean> = _isSshConnected.asStateFlow()

    private val _isProxyConnected = MutableStateFlow(false)
    val isProxyConnected: StateFlow<Boolean> = _isProxyConnected.asStateFlow()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var sshMonitorJob: Job? = null
    private var proxyMonitorJob: Job? = null

    fun pairMyIOS() {
        viewModelScope.launch(Dispatchers.IO) {
            sshService.triggerInit()
            iProxyService.startIProxy()
            delay(350.milliseconds)

            val connected = sshService.connect()
            if (!connected) {
                iProxyService.stopIProxy()
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
            iProxyService.stopIProxy()
            _isSshConnected.value = false
            _isProxyConnected.value = false
        }
    }

    private fun monitorConnection() {
        sshMonitorJob?.cancel()
        proxyMonitorJob?.cancel()

        proxyMonitorJob = iProxyService.monitorIProxyConnection()
            .onEach { _isProxyConnected.value = it }
            .launchIn(viewModelScope)

        sshMonitorJob = sshService.monitorSshConnection()
            .onEach { _isSshConnected.value = it }
            .launchIn(viewModelScope)
    }

    fun resetUiState(){
        _uiState.value = UiState.Idle
    }
}