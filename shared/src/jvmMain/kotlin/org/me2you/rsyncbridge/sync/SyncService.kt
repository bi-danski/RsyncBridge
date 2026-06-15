package org.me2you.rsyncbridge.sync

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import org.me2you.rsyncbridge.core.datastore.PreferenceRepo
import org.me2you.rsyncbridge.core.utils.logger
import org.me2you.rsyncbridge.data.SyncEvent
import org.me2you.rsyncbridge.data.SyncJob
import org.me2you.rsyncbridge.data.SyncOptions
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class SyncService(
    private val syncPrefs: PreferenceRepo,
    private val options: SyncOptions = SyncOptions()
) {
    private val currentConfig get() = syncPrefs.sshConfig.value
    private val processRef = AtomicReference<Process?>(null)

    private fun runSyncCmd(job: SyncJob, buildCommand: (SyncJob) -> List<String>): Flow<SyncEvent> {
        return flow {
            var process: Process? = null
            try {
                process = ProcessBuilder(buildCommand(job))
                    .redirectErrorStream(true)
                    .start()
                    .also { processRef.set(it) }

                val inputStream = process.inputStream
                val currentLine = StringBuilder()

                while (currentCoroutineContext().isActive) {
                    val b = inputStream.read()
                    if (b == -1) break

                    when (b.toChar()) {
                        '\n' -> {
                            emit(SyncEvent.Running(currentLine.toString() + "\n"))
                            currentLine.setLength(0)
                        }
                        '\r' -> {
                            if (currentLine.isNotEmpty()) {
                                emit(SyncEvent.Running(currentLine.toString()))
                                currentLine.setLength(0)
                            }
                        }
                        else -> currentLine.append(b.toChar())
                    }
                }

                if (currentLine.isNotEmpty()) emit(SyncEvent.Running(currentLine.toString()))
                currentCoroutineContext().ensureActive()

                val exitCode = process.waitFor()
                if (exitCode <= 0) emit(SyncEvent.Success("✅ Sync completed."))
                else emit(SyncEvent.Failed("Rsync failed. Exit code: $exitCode"))

            } catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) {
                logger().error("Rsync execution error", ex)
                emit(SyncEvent.Failed(ex.localizedMessage ?: "Unknown error"))

            } finally {
                processRef.set(null)
                process?.let {
                    if (it.isAlive) {
                        it.destroy()
                        if (!it.waitFor(3, TimeUnit.SECONDS)) {
                            it.destroyForcibly()
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun runSync(job: SyncJob): Flow<SyncEvent> = runSyncCmd(job, ::buildRsyncCmd)
    fun runSynciOS(job: SyncJob): Flow<SyncEvent> = runSyncCmd(job, ::buildRsynciOSCmd)
    fun cancel() { processRef.getAndSet(null)?.destroyForcibly() }

    private fun buildRsyncCmd(job: SyncJob): List<String> {
        return mutableListOf("sshpass", "-p", currentConfig.pass, "rsync", "-avP", "--info=progress2", "--stats")
            .apply {
                if (options.delete) add("--delete")
                options.exclude.forEach { add("--exclude=$it") }
                add("-e")
                add("ssh -p ${currentConfig.port} -o StrictHostKeyChecking=no")
                addAll(job.sources)
                add("${currentConfig.user}@${currentConfig.host}:${job.destination}")
            }
    }

    private fun buildRsynciOSCmd(job: SyncJob): List<String> {
        return mutableListOf("sshpass", "-p", currentConfig.pass, "rsync", "-avvP", "--info=progress2")
            .apply {
                if (options.delete) add("--delete")
                options.exclude.forEach { add("--exclude=$it") }
                add("-e")
                add("ssh -p ${currentConfig.port} -o StrictHostKeyChecking=no")
                job.sources.forEach { add("${currentConfig.user}@${currentConfig.host}:$it") }
                add(job.destination)
            }
    }
}