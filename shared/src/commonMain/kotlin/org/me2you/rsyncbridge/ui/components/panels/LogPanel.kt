package org.me2you.rsyncbridge.ui.components.panels

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.DeepCharcoal
import org.me2you.rsyncbridge.ui.theme.SurfaceGray
import org.me2you.rsyncbridge.ui.theme.TextPrimary
import org.me2you.rsyncbridge.ui.theme.TextSecondary

@Composable
fun LogPanel(logs: List<String>, fullScreen: Boolean = false) {
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) listState.animateScrollToItem(logs.lastIndex)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Rsync Transfer Logs",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (fullScreen) Modifier.fillMaxHeight() else Modifier.height(200.dp))
                    .clip(RoundedCornerShape(if (fullScreen) 0.dp else 20.dp))
                    .background(DeepCharcoal)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 10.dp),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    items(logs) { line ->
                        Text(
                            text = line,
                            fontFamily = FontFamily.SansSerif,
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    adapter = rememberScrollbarAdapter(scrollState = listState),
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