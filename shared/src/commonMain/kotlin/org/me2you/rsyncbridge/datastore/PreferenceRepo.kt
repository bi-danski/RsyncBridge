package org.me2you.rsyncbridge.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.me2you.rsyncbridge.logger.logger

class PreferenceRepo(
    private val dataStore: DataStore<SyncPreferences>,
    koinScope: CoroutineScope
) {
    private val syncPreferences: StateFlow<SyncPreferences> = dataStore.data
        .catch { if (it is IOException) emit(SyncPreferences()) else throw it }
        .stateIn(
            scope = koinScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SyncPreferences()
        )

    val iProxyConfig = syncPreferences
        .map { it.iProxyPrefs }
        .distinctUntilChanged()
        .stateIn(
            scope = koinScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = IProxyPreferences()
        )

    val sshConfig = syncPreferences
        .map { it.sshPrefs }
        .distinctUntilChanged()
        .stateIn(
            scope = koinScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SSHPreferences()
        )

    private suspend fun updateSyncPreferences(transform: (SyncPreferences) -> SyncPreferences){
        try {
            dataStore.updateData { transform(it) }
        } catch (ex: Exception){ logger().error("${this@PreferenceRepo} :: ${ex.message}") }
    }

    suspend fun updateIProxyPreferences(transform: (IProxyPreferences) -> IProxyPreferences){
        try {
            updateSyncPreferences {
                it.copy(iProxyPrefs = transform(it.iProxyPrefs))
            }
        } catch (ex: Exception){ logger().error("${this@PreferenceRepo} :: ${ex.message}") }
    }

    suspend fun updateSSHPreferences(transform: (SSHPreferences) -> SSHPreferences){
        try {
            updateSyncPreferences {
                it.copy(sshPrefs = transform(it.sshPrefs))
            }
        } catch (ex: Exception) { logger().error("${this@PreferenceRepo} :: ${ex.message}") }
    }

    suspend fun clearAllPreferences(){
        try {
            updateSyncPreferences {
                it.copy(
                    sshPrefs = SSHPreferences(),
                    iProxyPrefs = IProxyPreferences()
                )
            }
        } catch (ex: Exception) { logger().error("${this@PreferenceRepo} :: ${ex.message}") }
    }
}