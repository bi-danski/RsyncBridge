package org.me2you.rsyncbridge.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.me2you.rsyncbridge.core.logger.LogAppender
import org.me2you.rsyncbridge.core.utils.logger
import org.me2you.rsyncbridge.data.FileMetaData
import org.me2you.rsyncbridge.data.FileResult
import org.me2you.rsyncbridge.data.SyncEvent
import org.me2you.rsyncbridge.data.SyncJob
import org.me2you.rsyncbridge.data.SyncMode
import org.me2you.rsyncbridge.data.SyncStatus
import org.me2you.rsyncbridge.sync.FileManager
import org.me2you.rsyncbridge.sync.SyncService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class SyncViewModel(
    private val syncService: SyncService,
    private val fileManager: FileManager
) : ViewModel() {

//    fun getFileManager() = fileManager
    private var logAppender: LogAppender? = null
    private var activeSyncCoroutine: Job? = null

    private val _systemLogs: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val systemLogs: StateFlow<List<String>> = _systemLogs.asStateFlow()

    private val _syncLogs: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val syncLogs: StateFlow<List<String>> = _syncLogs.asStateFlow()

    private val _syncHistory: MutableStateFlow<MutableList<SyncJob>> = MutableStateFlow(mutableListOf())
    val syncHistory: StateFlow<MutableList<SyncJob>> = _syncHistory.asStateFlow()

    private val _syncJob: MutableStateFlow<SyncJob?> = MutableStateFlow(null)
    val activeSyncJob: StateFlow<SyncJob?> = _syncJob.asStateFlow()

    private val _syncMode: MutableStateFlow<SyncMode> = MutableStateFlow(SyncMode.D2IOS)
    val syncMode: StateFlow<SyncMode> = _syncMode.asStateFlow()

    init {
        val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        logAppender = LogAppender { viewModelScope.launch(Dispatchers.Main) { _systemLogs.value += it } }
            .also {
                it.context = rootLogger.loggerContext
                it.start()
                rootLogger.addAppender(it)
            }
    }

    override fun onCleared() {
        super.onCleared()
        val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        logAppender?.let { rootLogger.detachAppender(it) }
    }

    private fun updateSyncJob(transform: (SyncJob) -> SyncJob) {
        _syncJob.value = transform(_syncJob.value ?: SyncJob())
    }

    private fun updateSyncHistory(syncJob: SyncJob) {
        val indexOf = syncHistory.value.indexOfFirst { it.id == syncJob.id }
        if (indexOf == -1) syncHistory.value.addLast(syncJob)
        else syncHistory.value[indexOf] = syncJob
    }

    fun startSync() {
        val job = _syncJob.value ?: return
        if (job.sources.isEmpty() || job.destination.isBlank()) return

        job.status = SyncStatus.Running
        updateSyncJob { job }

        activeSyncCoroutine = viewModelScope.launch {
            val flow = when (_syncMode.value) {
                SyncMode.D2IOS -> syncService.runSync(job)
                SyncMode.IOS2D -> syncService.runSynciOS(job)
            }

            flow.collect { event ->
                when (event) {
                    is SyncEvent.Running -> {
                        if (job.status !is SyncStatus.Running) {
                            job.status = SyncStatus.Running
                            updateSyncJob { it.copy(status = SyncStatus.Running) }
                        }
                        val message = event.message ?: return@collect
                        if (message.isNotEmpty()) {
                            if (!message.endsWith("\n") && _syncLogs.value.isNotEmpty()) {
                                val updated = _syncLogs.value.toMutableList()
                                updated[updated.lastIndex] = message
                                _syncLogs.value = updated
                            } else _syncLogs.value += message.trimEnd('\n')
                        }
                    }

                    is SyncEvent.Success -> {
                        job.status = SyncStatus.Completed
                        updateSyncJob { it.copy(status = SyncStatus.Completed) }
                        _syncLogs.value += event.message
                        activeSyncCoroutine = null
                    }

                    is SyncEvent.Failed -> {
                        job.status = SyncStatus.Failed(event.reason)
                        updateSyncJob { it.copy(status = SyncStatus.Failed(event.reason)) }
                        _syncLogs.value += event.reason
                        _systemLogs.value += event.reason
                        activeSyncCoroutine = null
                        logger().warn { "@SyncEvent.Failed..: ${_syncJob.value!!.id}" }
                    }
                }
            }
        }

        updateSyncHistory(job)
    }

    @Suppress("unused")
    fun stopSync() {
        if (_syncJob.value == null || activeSyncCoroutine == null) return
        syncService.cancel()
        activeSyncCoroutine?.cancel()
        activeSyncCoroutine = null

        updateSyncJob { it.copy(status = SyncStatus.Cancelled) }
        _syncLogs.value += "🛑 Sync Cancelled."
        logger().warn { "@StopSync: Cancelled ${_syncJob.value?.id}" }
    }

    fun selectDesktopPath() {
        (fileManager.browseDesktopPath() as? FileResult.MetaData)?.items?.firstOrNull()?.let {
            updateSyncJob { job -> job.copy(destination = it.path) }
        }
    }

    fun selectDesktopFiles() {
        (fileManager.browseDesktopFiles() as? FileResult.MetaData)?.let {
            updateSyncJob { job ->
                job.copy(
                    sources = it.items.map { f -> f.path },
                    sourceFiles = it.items
                )
            }
        }
    }

    fun onIOSFilesSelected(files: List<FileMetaData>) {
        updateSyncJob { job ->
            job.copy(
                sources = files.map { file -> file.path },
                sourceFiles = files
            )
        }
    }

    fun onIOSPathSelected(path: String) { updateSyncJob { it.copy(destination = path) } }

    fun removeSourceFile(name: String) {
        updateSyncJob { job ->
            val filtered = job.sourceFiles.filter { it.name != name }
            job.copy(
                sources = filtered.map { it.path },
                sourceFiles = filtered
            )
        }
    }

    fun updateSyncMode() {
        if (_syncMode.value == SyncMode.IOS2D) _syncMode.value = SyncMode.D2IOS
        else _syncMode.value = SyncMode.IOS2D
    }

    fun onClear() { _syncJob.value = null }

    suspend fun onBrowseIOS(path: String): FileResult {
        return fileManager.browseIOS(path)

    }
}