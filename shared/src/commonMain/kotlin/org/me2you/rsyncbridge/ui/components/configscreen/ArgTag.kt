package org.me2you.rsyncbridge.ui.components.configscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.ui.theme.C2
import org.me2you.rsyncbridge.ui.theme.C4
import org.me2you.rsyncbridge.ui.theme.K2
import org.me2you.rsyncbridge.ui.theme.TextSecondary

@Composable
fun ArgTag(value: String, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = K2,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 0.5.dp,
                color = C2,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = value,
            fontSize = 12.sp,
            color = TextSecondary,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "×",
            fontSize = 14.sp,
            color = C4,
            modifier = Modifier.clickable { onRemove() }
        )
    }
}