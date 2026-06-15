package org.me2you.rsyncbridge

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import org.me2you.rsyncbridge.screens.RootScreen
import org.me2you.rsyncbridge.ui.theme.RsyncBridgeTheme

@Composable
fun RootJVM() {
    RsyncBridgeTheme {
        val rootController = rememberNavController()
        RootScreen(navController = rootController)
    }
}