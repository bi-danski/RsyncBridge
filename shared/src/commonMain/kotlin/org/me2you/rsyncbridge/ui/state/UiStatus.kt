package org.me2you.rsyncbridge.ui.state

@Suppress("unused")
sealed class UiSyncStatus {
    object Connecting : UiSyncStatus()
    object Connected: UiSyncStatus()
    data class Error(val message: String) : UiSyncStatus()
}

sealed class UiState {
//    data class Success(val message: String? = null): UiState()
    data class Error(val message: String? = null): UiState()
//    data class Loading(val message: String? = null): UiState()
    object Idle: UiState()
}