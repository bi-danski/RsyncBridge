package org.me2you.rsyncbridge.sync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.FileMode
import org.me2you.rsyncbridge.data.FileMetaData
import org.me2you.rsyncbridge.data.FileResult
import java.io.File
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

class FileManager(private val sshClient: SSHClient) {

    fun browseDesktopFiles(
        title: String = "Select Files",
        multiSelect: Boolean = true,
        initialPath: Path? = null,
        filters: List<FileFilter> = emptyList()
    ): FileResult {
        val chooser = JFileChooser().apply {
            dialogTitle = title
            fileSelectionMode = JFileChooser.FILES_ONLY
            isMultiSelectionEnabled = multiSelect
            initialPath?.let { currentDirectory = it.toFile() }
            filters.forEach { addChoosableFileFilter(it) }
            addHierarchyListener { _ ->
                val window = javax.swing.SwingUtilities.getWindowAncestor(this)
                window?.setSize(900, 600)
                window?.setLocationRelativeTo(null)
            }
        }

        return when (chooser.showOpenDialog(null)) {
            JFileChooser.APPROVE_OPTION -> {
                val files = if (multiSelect) chooser.selectedFiles else arrayOf(chooser.selectedFile)
                FileResult.MetaData(items = files.map { it.toMetaData() })
            }
            else -> FileResult.Cancelled
        }
    }

    fun browseDesktopPath(title: String = "Select Directory", initialPath: Path? = null): FileResult {
        val chooser = JFileChooser().apply {
            dialogTitle = title
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            isMultiSelectionEnabled = false
            initialPath?.let { currentDirectory = it.toFile() }
            addHierarchyListener { _ ->
                val window = javax.swing.SwingUtilities.getWindowAncestor(this)
                window?.setSize(900, 600)
                window?.setLocationRelativeTo(null)
            }
        }

        return when (chooser.showOpenDialog(null)) {
            JFileChooser.APPROVE_OPTION -> FileResult.MetaData(
                items = listOf(chooser.selectedFile.toMetaData()
                )
            )
            else -> FileResult.Cancelled
        }
    }

    suspend fun browseIOS(remotePath: String = "/var/mobile"): FileResult {
        return withContext(Dispatchers.IO) {
            if (!sshClient.isConnected) return@withContext FileResult.Error("Pair With iOS First !!")
            val sftp = sshClient.newSFTPClient()
            try {
                val items = sftp.ls(remotePath).map { file ->
                    FileMetaData(
                        name = file.name,
                        path = file.path,
                        isDirectory = file.attributes.type == FileMode.Type.DIRECTORY,
                        size = file.attributes.size,
                        permissions = file.attributes.mode.toString(),
                        owner = file.attributes.uid.toString(),
                        modifiedAt = file.attributes.mtime,
                    )
                }.sortedBy { it.modifiedAt }

                FileResult.MetaData(items)
            } catch (e: Exception) {
                FileResult.Error("Failed to list remote: ${e.message}")
            } finally { sftp.close() }
        }
    }

    suspend fun createIOSDirectory(remotePath: String): FileResult {
        return withContext(Dispatchers.IO) {
            if (!sshClient.isConnected) return@withContext FileResult.Error("Pair With iOS First !!")
            val sftp = sshClient.newSFTPClient()
            try {
                sftp.mkdir(remotePath)
                FileResult.MetaData(emptyList())
            } catch (e: Exception) {
                FileResult.Error("Failed to create directory: ${e.message}")
            } finally { sftp.close() }
        }
    }

    suspend fun deleteIOSDirectory(remotePath: String, recursive: Boolean = false): FileResult {
        return withContext(Dispatchers.IO) {
            if (!sshClient.isConnected) return@withContext FileResult.Error("Pair With iOS First !!")
            val sftp = sshClient.newSFTPClient()
            try {
                if (recursive) deleteIOSRecursive(sftp, remotePath)
                else sftp.rmdir(remotePath)
                FileResult.MetaData(emptyList())
            } catch (e: Exception) {
                FileResult.Error("Failed to delete directory: ${e.message}")
            } finally { sftp.close() }
        }
    }

//    fun createDesktopDirectory(path: String): FileResult {
//        return try {
//            val dir = File(path)
//            if (dir.exists()) return FileResult.Error("Directory already exists")
//            if (dir.mkdirs()) FileResult.MetaData(listOf(dir.toMetaData()))
//            else FileResult.Error("Failed to create directory")
//        } catch (e: Exception) {
//            FileResult.Error("Failed to create directory: ${e.message}")
//        }
//    }

//    fun deleteDesktopDirectory(path: String, recursive: Boolean = false): FileResult {
//        return try {
//            val dir = File(path)
//            if (!dir.exists()) return FileResult.Error("Directory does not exist")
//            if (!dir.isDirectory) return FileResult.Error("Path is not a directory")
//            val success = if (recursive) dir.deleteRecursively() else dir.delete()
//            if (success) FileResult.MetaData(emptyList())
//            else FileResult.Error("Failed to delete directory (may not be empty)")
//        } catch (e: Exception) {
//            FileResult.Error("Failed to delete directory: ${e.message}")
//        }
//    }

    private fun deleteIOSRecursive(sftp: net.schmizz.sshj.sftp.SFTPClient, remotePath: String) {
        val entries = sftp.ls(remotePath)
        for (entry in entries) {
            if (entry.name == "." || entry.name == "..") continue
            if (entry.attributes.type == FileMode.Type.DIRECTORY) {
                deleteIOSRecursive(sftp, entry.path)
                sftp.rmdir(entry.path)
            } else {
                sftp.rm(entry.path)
            }
        }
        sftp.rmdir(remotePath)
    }

    private fun File.toMetaData(): FileMetaData {
        return FileMetaData(
            name = this.name,
            path = this.absolutePath,
            isDirectory = this.isDirectory,
            size = if (this.isDirectory) 0L else this.length(),
            permissions = "${if (canRead()) "r" else "-"}${if (canWrite()) "w" else "-"}${if (canExecute()) "x" else "-"}",
            owner = System.getProperty("user.name"),
            modifiedAt = this.lastModified() / 1000L
        )
    }
}