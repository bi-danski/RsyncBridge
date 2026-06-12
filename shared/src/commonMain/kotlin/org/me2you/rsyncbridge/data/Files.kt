package org.me2you.rsyncbridge.data

sealed class FileResult {
    data class MetaData(val items: List<FileMetaData>) : FileResult()
    data class Error(val message: String) : FileResult()
    object Cancelled : FileResult()
}

data class FileMetaData(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val permissions: String = "",
    val owner: String = "",
    val modifiedAt: Long = 0L,
    val extension: String = if (!isDirectory)
        name.substringAfterLast(".", "") else "",
) {
    val formattedSize: String get() = when {
        isDirectory -> "—"
        size < 1_024 -> "$size B"
        size < 1_048_576 -> "${"%.1f".format(size / 1_024.0)} KB"
        size < 1_073_741_824 -> "${"%.1f".format(size / 1_048_576.0)} MB"
        else -> "${"%.2f".format(size / 1_073_741_824.0)} GB"
    }
}