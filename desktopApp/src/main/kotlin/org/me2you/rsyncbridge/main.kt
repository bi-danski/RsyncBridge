package org.me2you.rsyncbridge

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.me2you.rsyncbridge.di.KoinModule
import rsyncbridge.shared.generated.resources.Res
import rsyncbridge.shared.generated.resources.ic

fun main() {
    startKoin {
        modules(KoinModule.koinModules)
        printLogger(org.koin.core.logger.Level.INFO)
    }

    application {
        Window(
            onCloseRequest = {
                stopKoin()
                exitApplication()
            },
            title = "RsyncBridge",
            icon = painterResource(Res.drawable.ic),
            state = rememberWindowState(width = 900.dp, height = 730.dp),
        ) {
            RootJVM()
        }
    }
}