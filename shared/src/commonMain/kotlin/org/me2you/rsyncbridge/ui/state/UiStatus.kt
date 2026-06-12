package org.me2you.rsyncbridge.ui.state

@Suppress("unused")
sealed class UiSyncStatus {
    object Connecting : UiSyncStatus()
    object Connected: UiSyncStatus()
    data class Error(val message: String) : UiSyncStatus()
}