package com.example.cooldrop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cooldrop.filetransfer.composables.FileTransferScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = FileTransfer.route,
        modifier = modifier
    ) {
        composable(route = FileTransfer.route) {
            FileTransferScreen()
        }
    }
}