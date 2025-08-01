package com.meafrenchdude.quickse.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meafrenchdude.quickse.ui.screens.MainScreen
import com.meafrenchdude.quickse.ui.screens.KernelInfoScreen

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
