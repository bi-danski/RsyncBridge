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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.data.FileMetaData
import org.me2you.rsyncbridge.data.FileResult
import org.me2you.rsyncbridge.ui.components.DirectoryInfo
import org.me2you.rsyncbridge.ui.components.PathNavigator
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.C2
import org.me2you.rsyncbridge.ui.theme.K2

@Composable
fun IOSPathSelect(
    onBrowseIOS: suspend (String) -> FileResult,
    initialPath: String = "/var/mobile",
    onPathSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPath by remember { mutableStateOf(initialPath) }
    var fileList by remember { mutableStateOf<List<FileMetaData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(currentPath) {
        isLoading = true
        searchQuery = ""
        when (val result = onBrowseIOS(currentPath)) {
            is FileResult.MetaData -> {
                fileList = result.items.filter { it.isDirectory }
                errorMessage = null
            }
            is FileResult.Error -> errorMessage = result.message
            else -> {}
        }
        isLoading = false
    }

    val filteredDirs = remember(fileList, searchQuery) {
        if (searchQuery.isBlank()) fileList
        else fileList.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Destination Folder",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { onPathSelected(currentPath) },
                colors = ButtonDefaults.buttonColors(containerColor = ActionGreen),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(text = "Select Path")
            }

            IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (isSearchVisible) ActionGreen else Color.Gray
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

        Text(
            text = currentPath,
            color = ActionGreen.copy(alpha = 0.9f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )

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
                        text = "Filter folders...",
                        fontSize = 12.sp
                    ) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = K2,
                    unfocusedContainerColor = C2
                )
            )
        }

        PathNavigator(currentPath) { newPath -> currentPath = newPath }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                errorMessage != null -> Text(
                    text = "^_^\t $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
                filteredDirs.isEmpty() -> Text(
                    text = if (searchQuery.isBlank()) "^_^" else "No match for \"$searchQuery\"",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 12.dp)
                    ) {
                        items(filteredDirs) { dir ->
                            DirectoryInfo(
                                dir = dir,
                                onClick = { currentPath = dir.path }
                            )
                        }
                    }

                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState = listState),
                        style = ScrollbarStyle(
                            minimalHeight = 16.dp, thickness = 8.dp,
                            shape = RoundedCornerShape(4.dp),
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

