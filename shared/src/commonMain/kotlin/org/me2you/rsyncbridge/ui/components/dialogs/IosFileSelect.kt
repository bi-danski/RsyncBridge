package org.me2you.rsyncbridge.ui.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.data.FileMetaData
import org.me2you.rsyncbridge.data.FileResult
import org.me2you.rsyncbridge.ui.components.CompactFile
import org.me2you.rsyncbridge.ui.components.PathNavigator
import org.me2you.rsyncbridge.ui.state.SelectMode
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.C2
import org.me2you.rsyncbridge.ui.theme.K2

@Composable
fun IOSFileSelect(
    onBrowseIOS: suspend (String) -> FileResult,
    mode: SelectMode = SelectMode.SINGLE,
    initialPath: String = "/var/mobile",
    onFilesSelected: (List<FileMetaData>) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPath by remember { mutableStateOf(initialPath) }
    var fileList by remember { mutableStateOf<List<FileMetaData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentMode by remember { mutableStateOf(mode) }
    var isSearchVisible by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val selectedFiles = remember { mutableStateListOf<FileMetaData>() }

    LaunchedEffect(currentPath) {
        isLoading = true
        searchQuery = ""
        when (val result = onBrowseIOS(currentPath)) {
            is FileResult.MetaData -> {
                fileList = result.items
                errorMessage = null
            }
            is FileResult.Error -> errorMessage = result.message
            else -> {}
        }
        isLoading = false
    }

    val filteredFiles = remember(fileList, searchQuery) {
        if (searchQuery.isBlank()) fileList
            else fileList.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when(currentMode) {
                    SelectMode.MULTI -> "Multi-Select Files"
                    SelectMode.DIRECTORY -> "Select Destination"
                    else -> "Select File"
                },
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            if (currentMode == SelectMode.MULTI || currentMode == SelectMode.DIRECTORY) {
                Button(
                    onClick = {
                        if (currentMode == SelectMode.DIRECTORY) {
                            onFilesSelected(listOf(FileMetaData(
                                name = "Current Folder",
                                path = currentPath,
                                isDirectory = true,
                                size = 0)))
                        } else {
                            onFilesSelected(selectedFiles.toList())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionGreen),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text(
                        text = if (currentMode == SelectMode.DIRECTORY) "Select Folder" else "Confirm (${selectedFiles.size})"
                    )
                }
            }

            IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isSearchVisible) ActionGreen else Color.Gray
                )
            }

            IconButton(onClick = {
                currentMode = if (currentMode == SelectMode.MULTI) SelectMode.SINGLE else SelectMode.MULTI
                selectedFiles.clear()
            }) {
                Icon(
                    imageVector = Icons.Default.Checklist,
                    contentDescription = "Toggle Multi-Select",
                    tint = if (currentMode == SelectMode.MULTI) ActionGreen else Color.Gray
                )
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.Gray
                )
            }
        }

        AnimatedVisibility(
            visible = isSearchVisible,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 12.dp),
                placeholder = {
                    Text(
                        text = "Filter...",
                        fontSize = 12.sp
                    ) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = K2,
                    unfocusedContainerColor = C2
                )
            )
        }

        PathNavigator(currentPath) {
            newPath -> currentPath = newPath
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                errorMessage != null -> Text(
                    text = "^_^\t $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                    )
                filteredFiles.isEmpty() -> Text(
                    "^_^",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray,
                )
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 12.dp)
                    ) {
                        items(filteredFiles) { file ->
                            val isSelected = selectedFiles.contains(file)
                            CompactFile(
                                file = file,
                                isSelected = isSelected,
                                onClick = {
                                    if (file.isDirectory) {
                                        currentPath = file.path
                                    } else {
                                        when (currentMode) {
                                            SelectMode.SINGLE -> onFilesSelected(listOf(file))
                                            SelectMode.MULTI -> {
                                                if (isSelected) selectedFiles.remove(file)
                                                else selectedFiles.add(file)
                                            }
                                            else -> {}
                                        }
                                    }
                                }
                            )
                        }
                    }

                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState = listState),
                        style = ScrollbarStyle(
                            minimalHeight = 16.dp,
                            thickness = 8.dp,
                            shape = RoundedCornerShape(size = 4.dp),
                            hoverDurationMillis = 300,
                            unhoverColor = ActionGreen.copy(alpha = 0.5f),
                            hoverColor = ActionGreen
                        )
                    )
                }
            }
        }
    }
}

