package org.me2you.rsyncbridge.ui.components.historyscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.data.SyncStatus
import org.me2you.rsyncbridge.ui.theme.ActionCyan
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.ErrorRed
import org.me2you.rsyncbridge.ui.theme.K1
import org.me2you.rsyncbridge.ui.theme.K3
import org.me2you.rsyncbridge.ui.theme.K4
import org.me2you.rsyncbridge.ui.theme.K5
import org.me2you.rsyncbridge.ui.theme.K9

@Composable
fun StatusBadge(status: SyncStatus) {
    val style = when (status) {
        SyncStatus.Completed -> Badge("completed", K3, ActionGreen, ActionGreen)
        SyncStatus.Running -> Badge("running", K4, ActionCyan, ActionCyan)
        is SyncStatus.Failed, SyncStatus.Cancelled -> Badge("failed", K5, ErrorRed, ErrorRed)
        SyncStatus.Pending -> Badge("pending", K1, K9, K9)
        SyncStatus.Idle -> Badge("idle", Color.Gray, Color.Gray, Color.White.copy(0.5f))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(style.bg, RoundedCornerShape(4.dp))
            .border(0.5.dp, style.border, RoundedCornerShape(4.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        if (status == SyncStatus.Running) {
            PulseDot(color = ActionCyan)
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = style.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = style.fg,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.3.sp
        )
    }
}

