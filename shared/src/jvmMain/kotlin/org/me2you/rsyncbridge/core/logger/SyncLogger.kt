package org.me2you.rsyncbridge.core.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
class SyncLogger(clazz: Class<*>) {
    private val logger: Logger = LoggerFactory.getLogger(clazz)

    fun info(message: () -> String) {
        if (logger.isInfoEnabled) logger.info(message())
    }

    fun debug(message: () -> String) {
        if (logger.isDebugEnabled) logger.debug(message())
    }

    fun error(message: String, throwable: Throwable? = null) {
        if (logger.isErrorEnabled) {
            if (throwable != null) logger.error("⚠️ $message", throwable)
             else logger.error("⚠️ $message")
        }
    }

    fun warn(message: () -> String) {
        if (logger.isWarnEnabled) logger.warn(message())
    }

    fun imetii(message: () -> String){
        if (logger.isInfoEnabled) logger.info("✅ ${message()}")
    }
}