package org.me2you.rsyncbridge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.TextPrimary

@Composable
fun StatusNode(label: String, isActive: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(if (isActive) 10.dp else 8.dp)
                .clip(CircleShape)
                .background(color = if (isActive) ActionGreen else TextPrimary.copy(alpha = 0.2f))
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) ActionGreen else TextPrimary.copy(alpha = 0.6f),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}