package org.me2you.rsyncbridge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun RsyncBridgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RsyncColorScheme,
        content = content,
        typography = Typography
    )
}