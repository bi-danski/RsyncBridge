package org.me2you.rsyncbridge.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.me2you.rsyncbridge.navigation.RootNavigation
import org.me2you.rsyncbridge.ui.components.panels.SidePanel
import org.me2you.rsyncbridge.ui.theme.DeepCharcoal

@Composable
fun RootScreen(navController: NavHostController) {
    Scaffold(containerColor = DeepCharcoal) { padding ->
        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            SidePanel(navController = navController)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                RootNavigation(navController = navController)
            }
        }
    }
}