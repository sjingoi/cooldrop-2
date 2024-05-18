package com.example.cooldrop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cooldrop.filetransfer.composables.FileTransferScreen
import com.example.cooldrop.nameselect.composables.NameSelectScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NameSelect.route,
        modifier = modifier
    ) {
        composable(route = FileTransfer.route) {
            FileTransferScreen()
        }
        composable(route = NameSelect.route) {
            NameSelectScreen()
        }
    }
}