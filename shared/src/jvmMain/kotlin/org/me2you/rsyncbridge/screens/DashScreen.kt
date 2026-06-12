package org.me2you.rsyncbridge.screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.me2you.rsyncbridge.ui.components.dialogs.IOSFileSelect
import org.me2you.rsyncbridge.ui.components.dialogs.IOSPathSelect
import org.me2you.rsyncbridge.ui.components.dialogs.iOSDialog
import org.me2you.rsyncbridge.ui.components.panels.JobPanel
import org.me2you.rsyncbridge.ui.components.panels.LogPanel
import org.me2you.rsyncbridge.ui.components.panels.SyncPanel
import org.me2you.rsyncbridge.ui.components.panels.TopPanel
import org.me2you.rsyncbridge.ui.state.IosDialog
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.TextSecondary
import org.me2you.rsyncbridge.vm.RootViewModel
import org.me2you.rsyncbridge.vm.SyncViewModel

@Composable
fun DashScreen(
    rootViewModel: RootViewModel,
    syncViewModel: SyncViewModel
) {
    var iosDialog by remember { mutableStateOf<IosDialog>(IosDialog.None) }
    val syncJob by syncViewModel.activeSyncJob.collectAsStateWithLifecycle()
    val syncMode by syncViewModel.syncMode.collectAsStateWithLifecycle()
    val syncLogs by syncViewModel.syncLogs.collectAsStateWithLifecycle()
    val isSshActive by rootViewModel.isSshConnected.collectAsStateWithLifecycle()
    val isIProxyActive by rootViewModel.isProxyConnected.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val localScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TopPanel(
                    isSsh = isSshActive,
                    isIproxy = isIProxyActive,
                    onDisconnect = { if (isIProxyActive || isSshActive) rootViewModel.unPairMyIOS() },
                    onPairIOS = { if (!isIProxyActive) rootViewModel.pairMyIOS() }
                )
            }

            item {
                SyncPanel(
                    syncJob = syncJob,
                    syncMode = syncMode,
                    onClear = { syncViewModel.onClear() },
                    onRsync = { syncViewModel.startSync() },
                    onIOS = { iosDialog = IosDialog.FileSelect },
                    onIOSPath = { iosDialog = IosDialog.PathSelect },
                    onDesktop = { syncViewModel.selectDesktopFiles() },
                    onDesktopPath = { syncViewModel.selectDesktopPath() },
                    onSwap = { syncViewModel.updateSyncMode() }
                )
            }

            item {
                JobPanel(
                    syncJob = syncJob,
                    syncMode = syncMode,
                    onRemoveFile = { syncViewModel.removeSourceFile(it) }
                )
            }

            item { LogPanel(logs = syncLogs) }
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
    val dismiss = { iosDialog = IosDialog.None }

    when (iosDialog) {
        IosDialog.FileSelect -> iOSDialog(onDismiss = dismiss) {
            IOSFileSelect(
                onBrowseIOS = { path ->
                    syncViewModel.onBrowseIOS(path)
               },
                onFilesSelected = { files ->
                    syncViewModel.onIOSFilesSelected(files)
                    dismiss()
                },
                onDismiss = dismiss
            )
        }
        IosDialog.PathSelect -> iOSDialog(onDismiss = dismiss) {
            IOSPathSelect(
                onBrowseIOS = { path ->
                    syncViewModel.onBrowseIOS(path)
                },
                onPathSelected = { path ->
                    syncViewModel.onIOSPathSelected(path)
                    dismiss()
                },
                onDismiss = dismiss
            )
        }
        IosDialog.None -> Unit
    }
}