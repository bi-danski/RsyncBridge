package org.me2you.rsyncbridge.logger

fun <T : Any> T.logger(): SyncLogger = SyncLogger(this::class.java)
