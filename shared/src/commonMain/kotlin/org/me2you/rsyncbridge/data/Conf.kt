package org.me2you.rsyncbridge.data

import org.me2you.rsyncbridge.datastore.IProxyPreferences
import org.me2you.rsyncbridge.datastore.SSHPreferences

data class ProxyConfig(
    val remotePort: Int,
    val localPort: Int,
    val args: List<String>
){
    fun toIProxyPreferences(): IProxyPreferences {
        return IProxyPreferences(
            remotePort = this.remotePort,
            localPort = this.localPort,
            args = this.args
        )
    }
}

data class SshConfig(
    val host: String,
    val port: Int,
    val user: String,
    val pass: String
){
    fun toSSHPreferences(): SSHPreferences {
        return SSHPreferences(
            host = this.host,
            user = this.user,
            port = this.port,
            pass = this.pass
        )
    }
}