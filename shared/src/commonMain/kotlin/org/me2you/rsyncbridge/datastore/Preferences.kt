package org.me2you.rsyncbridge.datastore

import kotlinx.serialization.Serializable

@Serializable
data class SyncPreferences(
    val iProxyPrefs: IProxyPreferences = IProxyPreferences(),
    val sshPrefs: SSHPreferences = SSHPreferences()
)

@Serializable
data class IProxyPreferences(
    val remotePort: Int = 44,
    val localPort: Int = 4444,
    val args: List<String> = listOf("-d")
)

@Serializable
data class SSHPreferences(
    val host: String = "127.0.0.1",
    val port: Int = 4444,
    val user: String = "root",
    val pass: String = ""
)
