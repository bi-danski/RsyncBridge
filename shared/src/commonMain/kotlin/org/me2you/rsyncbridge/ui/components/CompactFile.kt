package org.me2you.rsyncbridge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.data.FileMetaData
import org.me2you.rsyncbridge.ui.theme.ActionGreen

@Composable
fun CompactFile(file: FileMetaData, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                color = if (isSelected) ActionGreen.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (file.isDirectory) ActionGreen else if (isSelected) ActionGreen else Color.Gray
        )
        Spacer(Modifier.width(20.dp))

        Text(
            text = file.name,
            color = if (isSelected) ActionGreen else Color.White,
            fontSize = 16.sp
        )

        Row(modifier = Modifier
            .weight(0.12f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = file.formattedSize,
                color = if (isSelected) ActionGreen else Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
            )
        }
    }
}