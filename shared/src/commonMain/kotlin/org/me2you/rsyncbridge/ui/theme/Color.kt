package org.me2you.rsyncbridge.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme

val DeepCharcoal = Color(0xFF121214)
val SurfaceGray = Color(0xFF1C1C20)
val SurfaceVariant = Color(0xFF2D2D35)
val ActionGreen = Color(0xFF00E676)
val ActionCyan = Color(0xFF00B8D4)
val ErrorRed = Color(0xFFFF5252)
val TextPrimary = Color(0xFFE0E0E0)
val TextSecondary = Color(0xFFAAAAAA)

val K1 = Color(0xFF2a200d)
val K2 = Color(0xFF1E1E21)
val K3 = Color(0xFF0d2a1a)
val K4 = Color(0xFF0d1e2a)
val K5 = Color(0xFF2a0d0d)
val K6 = Color(0xFF3D3D45)
val K8 = Color(0xFF0d2a2e)
val K9 = Color(0xFFE5A34E)

val C2 = Color(0xFF3D3D45)
val C4 = Color(0xFF555555)
val C8 = Color(0xFFFF9400)
val C9 = Color(0xFF555560)

val RsyncColorScheme = darkColorScheme(
    primary = ActionGreen,
    secondary = ActionCyan,
    background = DeepCharcoal,
    surface = SurfaceGray,
    surfaceVariant = SurfaceVariant,
    onPrimary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed
)