package org.me2you.rsyncbridge.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import org.me2you.rsyncbridge.data.ConfigRoute
import org.me2you.rsyncbridge.data.DashboardRoute
import org.me2you.rsyncbridge.data.HistoryRoute
import org.me2you.rsyncbridge.data.LogsRoute
import org.me2you.rsyncbridge.screens.ConfigScreen
import org.me2you.rsyncbridge.screens.DashScreen
import org.me2you.rsyncbridge.screens.HistoryScreen
import org.me2you.rsyncbridge.screens.LogScreen
import org.me2you.rsyncbridge.vm.ConfViewModel
import org.me2you.rsyncbridge.vm.RootViewModel
import org.me2you.rsyncbridge.vm.SyncViewModel

@Composable
fun RootNavigation(navController: NavHostController){
    val vmOwner = LocalLifecycleOwner.current as ViewModelStoreOwner
    val syncViewModel = koinViewModel<SyncViewModel>(viewModelStoreOwner = vmOwner)
    val confViewModel = koinViewModel<ConfViewModel>(viewModelStoreOwner = vmOwner)
    val rootViewModel = koinViewModel<RootViewModel>(viewModelStoreOwner = vmOwner)

    NavHost(
        navController = navController,
        startDestination = DashboardRoute
    ) {
        composable<DashboardRoute> {
            DashScreen(
                rootViewModel = rootViewModel,
                syncViewModel = syncViewModel
            )
        }
        composable<ConfigRoute> {
            ConfigScreen(confViewModel = confViewModel)
        }
        composable<HistoryRoute> {
            HistoryScreen(syncViewModel = syncViewModel)
        }
        composable<LogsRoute> {
            LogScreen(viewModel = syncViewModel)
        }
    }
}