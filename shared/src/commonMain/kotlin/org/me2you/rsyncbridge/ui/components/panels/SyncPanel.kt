package org.me2you.rsyncbridge.ui.components.panels

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.data.SyncJob
import org.me2you.rsyncbridge.data.SyncMode
import org.me2you.rsyncbridge.data.SyncStatus
import org.me2you.rsyncbridge.ui.components.PPathInfo
import org.me2you.rsyncbridge.ui.theme.ActionCyan
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.C8
import org.me2you.rsyncbridge.ui.theme.ErrorRed
import org.me2you.rsyncbridge.ui.theme.SurfaceGray

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SyncPanel(
    syncJob: SyncJob?,
    syncMode: SyncMode,
    onSwap: () -> Unit,
    onClear: () -> Unit,
    onRsync: () -> Unit = {},
    onIOS: () -> Unit,
    onIOSPath: () -> Unit,
    onDesktop: () -> Unit,
    onDesktopPath: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.8f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onRsync,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = animateColorAsState(
                            targetValue = when(syncJob?.status){
                                is SyncStatus.Failed -> ErrorRed.copy(0.15f)
                                SyncStatus.Pending, SyncStatus.Running -> ActionCyan.copy(0.15f)
                                SyncStatus.Completed -> ActionGreen.copy(0.15f)
                                else -> ActionCyan
                            }
                        ).value
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Start Rsync Service",
                        tint = animateColorAsState(targetValue = when(syncJob?.status) {
                            SyncStatus.Pending -> Color.Yellow
                            SyncStatus.Completed -> ActionGreen.copy(0.55f)
                            SyncStatus.Running -> ActionGreen
                            SyncStatus.Cancelled -> Color.Red.copy(0.85f)
                            is SyncStatus.Failed -> ErrorRed.copy(0.85f)
                            else -> Color.Black
//                            SyncStatus.Idle -> Color.Black
                        }).value,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = when (syncJob?.status) {
                            SyncStatus.Running -> "STOP RSYNC"
                            else -> "START RSYNC"
                        },
                        color = animateColorAsState(targetValue = when(syncJob?.status) {
                            SyncStatus.Pending -> Color.Yellow
                            SyncStatus.Completed-> ActionGreen.copy(0.55f)
                            SyncStatus.Running -> ActionGreen
                            SyncStatus.Cancelled -> Color.Red.copy(0.85f)
                            is SyncStatus.Failed -> ErrorRed.copy(0.85f)
                            else -> Color.Black
//                            SyncStatus.Idle -> Color.Black
                        }).value,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .width(1.dp)
                .background(Color.White.copy(alpha = 0.12f))
            )

            Column(
                modifier = Modifier.weight(2.3f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (syncMode == SyncMode.IOS2D) {
                    PPathInfo(
                        label = "Source",
                        path = syncJob?.sourceDisplay ?: "Not Set",
                        icon = Icons.Default.Smartphone,
                        onEdit = onIOS
                    )
                    PPathInfo(
                        label = "Destination",
                        path = syncJob?.destination ?: "Not Set",
                        icon = Icons.Default.Computer,
                        onEdit = onDesktopPath
                    )
                } else {
                    PPathInfo(
                        label = "Source",
                        path = syncJob?.sourceDisplay ?: "Not Set",
                        icon = Icons.Default.Computer,
                        onEdit = onDesktop
                    )
                    PPathInfo(
                        label = "Destination",
                        path = syncJob?.destination ?: "Not Set",
                        icon = Icons.Default.Smartphone,
                        onEdit = onIOSPath
                    )
                }
            }

            Box(modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .width(1.dp)
                .background(Color.White.copy(alpha = 0.12f))
            )

            Column(
                modifier = Modifier.weight(0.1f),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "New Transfer",
                            tint = Color.Red.copy(0.55f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onSwap) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap Transfer Mode",
                            tint = C8,
                            modifier = Modifier.size(29.dp)
                        )
                    }
                }
            }
        }
    }
}