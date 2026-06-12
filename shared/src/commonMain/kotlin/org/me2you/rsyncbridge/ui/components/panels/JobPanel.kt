package org.me2you.rsyncbridge.ui.components.panels

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.data.SyncJob
import org.me2you.rsyncbridge.data.SyncMode
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.SurfaceGray
import org.me2you.rsyncbridge.ui.theme.TextPrimary
import org.me2you.rsyncbridge.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobPanel(
    syncJob: SyncJob?,
    syncMode: SyncMode,
    onRemoveFile: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val internalListState = rememberLazyListState()
    val expandedHeight = minOf((47.dp * (syncJob?.sourceFiles?.size ?: 0)) + 8.dp, 200.dp)
    val collapsedHeight = minOf(expandedHeight, (47.dp * 3) + 8.dp)
//    val expandedHeight = minOf((44.dp * (syncJob?.sourceFiles?.size ?: 0)) + 8.dp, 200.dp)

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (syncJob == null) "Zero Active Jobs"
                            else
                                if (syncMode == SyncMode.IOS2D) "iOS -> Linux" else "Linux -> iOS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Estimated Total: ${syncJob?.formattedTotalSize ?: "0 B"} / ${syncJob?.sources?.size ?: 0} Files",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                if (syncJob?.sources?.isNotEmpty() == true) {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle Size",
                            tint = TextPrimary
                        )
                    }
                }
            }

            if (syncJob?.sources != null && syncJob.sources.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isExpanded) expandedHeight else collapsedHeight)
//                        .height(if (isExpanded) expandedHeight else 55.dp)
                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    LazyColumn(
                        state = internalListState,
                        modifier = Modifier.fillMaxSize().padding(end = 10.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(syncJob.sourceFiles) { syncFile ->
                            val dismissState = rememberSwipeToDismissBoxState()

                            LaunchedEffect(dismissState.currentValue) {
                                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                    onRemoveFile(syncFile.name)
                                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                }
                            }

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                animateColorAsState(
                                                    targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                                        Color.Red.copy(alpha = 0.8f) else Color.Transparent,
                                                    label = "dismiss_color"
                                                ).value,
                                                RoundedCornerShape(4.dp))
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(Icons.Default.Delete, "Delete", tint = Color.White)
                                    }
                                }
                            ) {
                                Surface(color = SurfaceGray, modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "\uD83D\uDCC2 ${syncFile.path}",
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.Left,
                                            overflow = TextOverflow.MiddleEllipsis
                                        )

                                        Text(
                                            text = syncFile.formattedSize,
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                            modifier = Modifier.weight(0.2f),
                                            textAlign = TextAlign.Right
                                        )
                                    }
                                }
                            }
                        }
                    }

                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        adapter = rememberScrollbarAdapter(scrollState = internalListState),
                        style = defaultScrollbarStyle().copy(
                            unhoverColor = TextSecondary.copy(alpha = 0.2f),
                            hoverColor = ActionGreen.copy(alpha = 0.5f),
                            thickness = 8.dp
                        )
                    )
                }
            }
        }
    }
}