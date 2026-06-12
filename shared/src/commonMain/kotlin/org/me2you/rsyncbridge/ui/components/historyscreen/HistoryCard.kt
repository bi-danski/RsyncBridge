package org.me2you.rsyncbridge.ui.components.historyscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.data.SyncJob
import org.me2you.rsyncbridge.data.SyncMode
import org.me2you.rsyncbridge.data.SyncStatus
import org.me2you.rsyncbridge.ui.theme.ErrorRed
import org.me2you.rsyncbridge.ui.theme.SurfaceGray
import org.me2you.rsyncbridge.ui.theme.SurfaceVariant
import org.me2you.rsyncbridge.ui.theme.TextSecondary

@Composable
fun HistoryCard(job: SyncJob, mode: SyncMode) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SurfaceGray,
                shape = RoundedCornerShape(10.dp))
            .border(
                width = 0.5.dp,
                color = SurfaceVariant,
                shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (mode) {
                    SyncMode.IOS2D -> "IOS → Linux"
                    SyncMode.D2IOS -> "Linux → IOS"
                },
                fontSize = 11.sp,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )

            StatusBadge(status = job.status)
        }

        PathInfo(label = "SOURCE", path = job.sources.firstOrNull() ?: "—")
        Spacer(modifier = Modifier.height(5.dp))
        PathInfo(label = "DEST", path = job.destination.ifBlank { "—" })

        HorizontalDivider(
            modifier = Modifier.padding(top = 10.dp),
            thickness = 0.5.dp,
            color = SurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${job.sourceFiles.size} file${if (job.sourceFiles.size != 1) "s" else ""}",
                fontSize = 11.sp,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace
            )
            if (job.status is SyncStatus.Failed) {
                Text(
                    text = (job.status as SyncStatus.Failed).reason.take(40),
                    fontSize = 11.sp,
                    color = ErrorRed,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}