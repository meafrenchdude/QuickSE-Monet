package com.maazm7d.quickse.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maazm7d.quickse.ui.screens.MainScreen
import com.maazm7d.quickse.ui.screens.KernelInfoScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("kernel_info") {
            KernelInfoScreen()
        }
    }
}
