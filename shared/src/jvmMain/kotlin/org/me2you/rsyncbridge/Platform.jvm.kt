package org.me2you.rsyncbridge

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

@Suppress("unused")
actual fun getPlatform(): Platform = JVMPlatform()