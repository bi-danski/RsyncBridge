package org.me2you.rsyncbridge.ui.components.panels

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.me2you.rsyncbridge.ui.components.StatusNode
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.ErrorRed
import org.me2you.rsyncbridge.ui.theme.K9
import org.me2you.rsyncbridge.ui.theme.SurfaceGray
import org.me2you.rsyncbridge.ui.theme.SurfaceVariant

@Composable
fun TopPanel(
    isSsh: Boolean,
    isIproxy: Boolean,
    onDisconnect: () -> Unit,
//    onStopSync: () -> Unit,
    onPairIOS: () -> Unit
) {
    val isConnected = isSsh && isIproxy

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .clip(RoundedCornerShape(16.dp)),
        color = SurfaceGray.copy(alpha = 0.4f),
        border = BorderStroke(width = 1.dp, color = SurfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onPairIOS ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = animateColorAsState(
                            targetValue = if (isConnected) ActionGreen.copy(alpha = 0.15f) else
                                MaterialTheme.colorScheme.primary.copy(0.35f)
                        ).value,
                        contentColor = animateColorAsState(
                            targetValue = if (isConnected) ActionGreen else Color.White
                        ).value
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    border = if (isConnected) BorderStroke(1.dp, ActionGreen.copy(alpha = 0.3f)) else null
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.Link else Icons.Default.LinkOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) "Connected" else "Pair iOS",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSsh || isIproxy) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }

//                ControlChip(
//                    label = "Desktop",
//                    containerColor = SurfaceVariant,
//                    labelColor = TextPrimary,
//                    onClick = onDesktop
//                )

//                ControlChip(
//                    label = "iOS",
//                    containerColor = SurfaceVariant,
//                    labelColor = TextPrimary,
//                    onClick = onIOS
//                )

                Button(
                    onClick = onDisconnect ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = animateColorAsState(
                            targetValue = SurfaceVariant.copy(0.5f)).value,
                        contentColor = animateColorAsState(
                            targetValue = if (isSsh || isIproxy) K9.copy(0.89f) else Color.White).value
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
                ) {
                    if (isSsh || isIproxy) {
                        Icon(
                            imageVector = Icons.Default.LinkOff,
                            contentDescription = "Un-Pair IOS",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Disconnect",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSsh || isIproxy) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }

                Button(
                    onClick = onDisconnect ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = animateColorAsState(
                            targetValue = SurfaceVariant.copy(0.5f)
                        ).value,
                        contentColor = animateColorAsState(
                            targetValue = if (isSsh || isIproxy) K9.copy(0.89f) else Color.White
                        ).value
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
                ) {
                    if (isSsh || isIproxy) {
                        Icon(
                            imageVector = Icons.Default.LinkOff,
                            contentDescription = "Un-Pair IOS",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Stop Rsync",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSsh || isIproxy) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = 12.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusNode(label = "iProxy", isActive = isIproxy)
                StatusNode(label = "SSH", isActive = isSsh)
            }
        }
    }
}

