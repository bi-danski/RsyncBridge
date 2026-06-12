package org.me2you.rsyncbridge.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ControlChip(
    label: String,
    containerColor: Color,
    labelColor: Color,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text = label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor.copy(alpha = 0.15f),
            labelColor = labelColor
        )
    )
}
