package org.me2you.rsyncbridge.core.utils

import java.io.File
import java.nio.file.Files

object BinUtil{
    fun getWindowsArch(osArch: String): String = when {
        osArch.contains("x86_64") || osArch.contains("amd64") -> "x64"
        osArch.contains("x86") || osArch.contains("i386") -> "x86"
        osArch.contains("aarch64") -> "arm64"
        else -> throw UnsupportedOperationException("Unsupported Windows architecture: $osArch")
    }

    fun getMacArch(osArch: String): String = when {
        osArch.contains("x86_64") -> "x64"
        osArch.contains("aarch64") || osArch.contains("arm64") -> "arm64"
        else -> throw UnsupportedOperationException("Unsupported macOS architecture: $osArch")
    }

    fun getLinuxArch(osArch: String): String = when {
        osArch.contains("x86_64") || osArch.contains("amd64") -> "x64"
        osArch.contains("x86") || osArch.contains("i386") -> "x86"
        osArch.contains("aarch64") -> "arm64"
        osArch.contains("armv7l") || osArch.contains("armv7") -> "armv7"
        else -> throw UnsupportedOperationException("Unsupported Linux architecture: $osArch")
    }

    fun resolveBinaryPath(platform: String, arch: String, binaryName: String): String {
        val resourcesDir = File(System.getProperty("user.dir"))
            .resolve("shared/src/jvmMain/resources")
            .resolve(platform)
            .resolve(arch)
            .resolve(binaryName)

        if (resourcesDir.exists() && resourcesDir.isFile) return resourcesDir.absolutePath
        val cwdPath = File(System.getProperty("user.dir"))
            .resolve("resources")
            .resolve(platform)
            .resolve(arch)
            .resolve(binaryName)

        if (cwdPath.exists() && cwdPath.isFile) return cwdPath.absolutePath

        val classpathResource = this::class.java.classLoader.getResource("$platform/$arch/$binaryName")
        if (classpathResource != null) {
            val tempFile = try {
                val resourceStream = classpathResource.openStream()
                val suffix = if (System.getProperty("os.name").lowercase().contains("win")) ".exe" else ""
                val tempBinary = Files.createTempFile("iproxy", suffix)
                resourceStream.use { input ->
                    Files.newOutputStream(tempBinary).use { output ->
                        input.copyTo(output)
                    }
                }
                tempBinary.toFile().apply { deleteOnExit() }
            } catch (ex: Exception) {
                logger().debug { "Failed to extract classpath resource: ${ex.message}" }
                null
            }
            if (tempFile != null) return tempFile.absolutePath
        }

        val appHome = File(System.getProperty("user.dir"))
        val searchDirs = listOf(
            appHome.resolve("resources/$platform/$arch/$binaryName"),
            appHome.parentFile?.resolve("resources/$platform/$arch/$binaryName"),
            File("/opt/rsyncbridge/resources/$platform/$arch/$binaryName"),
            File("${System.getProperty("user.home")}/RsyncBridge/resources/$platform/$arch/$binaryName")
        )

        for (path in searchDirs) {
            if (path != null && path.exists() && path.isFile) return path.absolutePath
        }

        return resourcesDir.absolutePath
    }
}