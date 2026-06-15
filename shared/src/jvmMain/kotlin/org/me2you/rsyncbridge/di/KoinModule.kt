package org.me2you.rsyncbridge.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import net.schmizz.sshj.SSHClient
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.me2you.rsyncbridge.core.crypto.CryptoGCM
import org.me2you.rsyncbridge.core.crypto.KeyRingProvider
import org.me2you.rsyncbridge.core.datastore.PreferenceRepo
import org.me2you.rsyncbridge.core.datastore.PreferenceSerializer
import org.me2you.rsyncbridge.data.SyncPreferences
import org.me2you.rsyncbridge.sync.FileManager
import org.me2you.rsyncbridge.sync.IProxyService
import org.me2you.rsyncbridge.sync.SSHService
import org.me2you.rsyncbridge.sync.SyncService
import org.me2you.rsyncbridge.vm.ConfViewModel
import org.me2you.rsyncbridge.vm.RootViewModel
import org.me2you.rsyncbridge.vm.SyncViewModel
import java.io.File
import java.security.Security

object KoinModule {

    init {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    val koinModules = module {

        single<File>(qualifier = named("appDataDir")) {
            File(System.getProperty("user.home"), ".rsyncbridge").also { it.mkdirs() }
        }

        single(createdAtStart = true) {
            CryptoGCM.init(
                get(named("appDataDir")),
                KeyRingProvider.getOrGeneratePasswd()
            )
        }

        single<CoroutineScope> {
            CoroutineScope(SupervisorJob() + Dispatchers.Default)
        } onClose { scope -> scope?.cancel() }

        single<DataStore<SyncPreferences>> {
            val appDataDir: File = get(named("appDataDir"))
            DataStoreFactory.create(
                serializer = PreferenceSerializer,
                scope = get<CoroutineScope>(),
                produceFile = { File(appDataDir, "_sync.pb") }
            )
        }

        single(createdAtStart = true) {
            PreferenceRepo(
                dataStore = get(),
                koinScope = get()
            )
        }

        single { SSHClient() }

        single { FileManager(sshClient = get()) }

        single { SyncService(syncPrefs = get()) }

        single {
            SSHService(
                syncPrefs = get(),
                sshClient = get()
            )
        } onClose { service -> service?.disconnect() }

        single {
            IProxyService(
                syncPrefs = get(),
                koinScope = get()
            )
        } onClose { iProxy -> iProxy?.stopIproxy() }

        viewModelOf(::RootViewModel)
        viewModelOf(::ConfViewModel)
        viewModelOf(::SyncViewModel)
    }
}