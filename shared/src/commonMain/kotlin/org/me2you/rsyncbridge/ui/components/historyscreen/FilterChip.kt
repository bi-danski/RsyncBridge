package org.me2you.rsyncbridge.ui.components.historyscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.ui.theme.ActionCyan
import org.me2you.rsyncbridge.ui.theme.K8
import org.me2you.rsyncbridge.ui.theme.SurfaceGray
import org.me2you.rsyncbridge.ui.theme.SurfaceVariant
import org.me2you.rsyncbridge.ui.theme.TextSecondary

@Composable
fun FilterChip(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (active) K8 else SurfaceGray,
                RoundedCornerShape(20.dp))
            .border(
                0.5.dp,
                if (active) ActionCyan else SurfaceVariant,
                RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (active) ActionCyan else TextSecondary,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
    }
}