package org.me2you.rsyncbridge.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.me2you.rsyncbridge.ui.theme.ActionGreen

@Composable
fun PathNavigator(path: String, onNavigate: (String) -> Unit) {
    val segments = if (path == "/") emptyList()
        else path.split("/").filter { it.isNotEmpty() }

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextButton(
            onClick = { onNavigate("/") },
            modifier = Modifier.padding(horizontal = 0.dp)
        ) {
            Text(
                text = "\uD83D\uDDA5\uFE0F",
                color = ActionGreen
            )
        }
        segments.forEachIndexed { index, segment ->
            Text(
                text = "/",
                color = Color.Gray
            )
            TextButton(
                onClick = {
                    onNavigate("/" + segments.take(index + 1).joinToString("/"))
                }
            ) {
                Text(
                    text = segment,
                    color = if (index == segments.lastIndex) Color.White else Color.Gray
                )
            }
        }
    }
}