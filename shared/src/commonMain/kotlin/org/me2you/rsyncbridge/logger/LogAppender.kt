package org.me2you.rsyncbridge.logger

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase

class LogAppender(
    private val onLog: (String) -> Unit
) : AppenderBase<ILoggingEvent>() {

    private lateinit var encoder: PatternLayoutEncoder

    override fun start() {
        encoder = PatternLayoutEncoder().apply {
            pattern = "%-5level %d{HH:mm:ss.SSS} [%thread] - %msg%n"
            context = this@LogAppender.context
            start()
        }
        super.start()
    }

    override fun append(event: ILoggingEvent) {
        val formatted = encoder.encode(event).toString(Charsets.UTF_8).trimEnd()
        onLog(formatted)
    }

    override fun stop() {
        encoder.stop()
        super.stop()
    }
}