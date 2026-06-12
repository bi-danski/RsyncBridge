package org.me2you.rsyncbridge.data

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class SyncOptions(
    val recursive: Boolean = true,
    val archive: Boolean = true,
    val verbose: Boolean = true,
    val compress: Boolean = true,
    val delete: Boolean = false,
    val exclude: List<String> = emptyList()
)

sealed class SyncEvent {
    data class Success(val message: String) : SyncEvent()
    data class Failed(val reason: String): SyncEvent()
    data class Running(val message: String? = null): SyncEvent()
}

sealed class SyncStatus {
    object Pending : SyncStatus()
    object Idle: SyncStatus()
    object Completed : SyncStatus()
    object Running: SyncStatus()
    object Cancelled: SyncStatus()
    data class Failed(val reason: String) : SyncStatus()
}

data class SyncJob @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.generateV7().toString(),
    val sources: List<String> = emptyList(),
    val sourceFiles: List<FileMetaData> = emptyList(),
    val destination: String = "",
    var status: SyncStatus = SyncStatus.Pending
) {
    val sourceDisplay: String get() = when {
        sources.isEmpty() -> "Not Set"
        sources.size == 1 -> sources.first()
        else -> "${sources.first()} (+${sources.size - 1} more)"
    }
    val totalSize: Long get() = sourceFiles.sumOf { it.size }
    val formattedTotalSize: String get() = when {
        sourceFiles.isEmpty() -> "—"
        totalSize < 1_024 -> "$totalSize B"
        totalSize < 1_048_576 -> "${"%.1f".format(totalSize / 1_024.0)} KB"
        totalSize < 1_073_741_824 -> "${"%.1f".format(totalSize / 1_048_576.0)} MB"
        else -> "${"%.2f".format(totalSize / 1_073_741_824.0)} GB"
    }
}