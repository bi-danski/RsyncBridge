package org.me2you.rsyncbridge.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.me2you.rsyncbridge.data.SyncStatus
import org.me2you.rsyncbridge.ui.components.SnackBar
import org.me2you.rsyncbridge.ui.components.historyscreen.FilterChip
import org.me2you.rsyncbridge.ui.components.historyscreen.HistoryCard
import org.me2you.rsyncbridge.ui.state.HistoryFilter
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.C9
import org.me2you.rsyncbridge.ui.theme.DeepCharcoal
import org.me2you.rsyncbridge.ui.theme.TextPrimary
import org.me2you.rsyncbridge.ui.theme.TextSecondary
import org.me2you.rsyncbridge.vm.SyncViewModel


@Composable
fun HistoryScreen(syncViewModel: SyncViewModel) {
    val history by syncViewModel.syncHistory.collectAsStateWithLifecycle()
    val syncMode by syncViewModel.syncMode.collectAsStateWithLifecycle()
    var activeFilter by remember { mutableStateOf(HistoryFilter.All) }
    val listState = rememberLazyListState()
    val hostState = remember { SnackbarHostState() }
    val filtered = remember(history.toList(), activeFilter) {
        when (activeFilter) {
            HistoryFilter.All -> history
            HistoryFilter.Completed -> history.filter { it.status == SyncStatus.Completed }
            HistoryFilter.Running -> history.filter { it.status == SyncStatus.Running }
            HistoryFilter.Pending -> history.filter { it.status == SyncStatus.Pending }
            HistoryFilter.Failed -> history.filter { it.status is SyncStatus.Failed }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = hostState,
                snackbar = { SnackBar(snackBarData = it) }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepCharcoal)
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "History",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Transfer job log",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            HistoryFilter.entries.forEach { filter ->
                                FilterChip(
                                    label = filter.name,
                                    active = activeFilter == filter,
                                    onClick = { activeFilter = filter }
                                )
                            }
                        }
                    }
                }

                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transfers found.",
                                fontSize = 13.sp,
                                color = C9,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    items(filtered) { job ->
                        HistoryCard(
                            job = job,
                            mode = syncMode
                        )
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                adapter = rememberScrollbarAdapter(scrollState = listState),
                style = defaultScrollbarStyle().copy(
                    unhoverColor = TextSecondary.copy(alpha = 0.3f),
                    hoverColor = ActionGreen.copy(alpha = 0.4f),
                    thickness = 8.dp,
                    shape = MaterialTheme.shapes.small
                )
            )
        }
    }
}