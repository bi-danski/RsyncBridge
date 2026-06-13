package org.me2you.rsyncbridge

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform