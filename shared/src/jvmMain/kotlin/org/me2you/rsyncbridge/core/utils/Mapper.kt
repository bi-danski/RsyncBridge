package org.me2you.rsyncbridge.core.utils

import org.me2you.rsyncbridge.core.logger.SyncLogger

fun <T : Any> T.logger(): SyncLogger = SyncLogger(this::class.java)
