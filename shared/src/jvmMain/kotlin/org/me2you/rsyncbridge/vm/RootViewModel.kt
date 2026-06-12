package org.me2you.rsyncbridge.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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

    fun pairMyIOS() {
        viewModelScope.launch(Dispatchers.IO) {
            iProxyService.startIproxy()
            delay(550.milliseconds)
            sshService.triggerInit()
            sshService.connect()

            launch(Dispatchers.Main) {
                monitorConnection()
            }
        }
    }

    fun unPairMyIOS() {
        viewModelScope.launch(Dispatchers.IO) {
            sshService.disconnect()
            iProxyService.stopIproxy()
        }
    }

    private fun monitorConnection(){
        iProxyService.monitorConnection()
            .onEach { _isProxyConnected.value = it }
            .launchIn(viewModelScope)

        sshService.monitorConnection()
            .onEach { _isSshConnected.value = it }
            .launchIn(viewModelScope)
    }

}