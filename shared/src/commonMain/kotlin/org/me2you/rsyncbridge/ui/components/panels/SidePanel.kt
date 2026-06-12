package org.me2you.rsyncbridge.ui.components.panels

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import org.me2you.rsyncbridge.data.ConfigRoute
import org.me2you.rsyncbridge.data.DashboardRoute
import org.me2you.rsyncbridge.data.HistoryRoute
import org.me2you.rsyncbridge.data.LogsRoute
import org.me2you.rsyncbridge.ui.theme.ActionGreen
import org.me2you.rsyncbridge.ui.theme.SurfaceGray
import org.me2you.rsyncbridge.ui.theme.TextSecondary

@Composable
fun SidePanel(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Triple(DashboardRoute, "Dashboard", Icons.Default.Dashboard),
        Triple(HistoryRoute, "History", Icons.Default.History),
        Triple(LogsRoute, "Logs", Icons.Default.Terminal),
        Triple(ConfigRoute, "Configs", Icons.Default.SettingsApplications)
        )

    NavigationRail(
        containerColor = SurfaceGray,
        modifier = Modifier.width(80.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        items.forEach { (route, label, icon) ->
            NavigationRailItem(
                selected = currentDestination?.hasRoute(route::class) == true,
                onClick = { navController.navigate(route) { launchSingleTop = true } },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = ActionGreen,
                    unselectedIconColor = TextSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
