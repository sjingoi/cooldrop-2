package com.example.cooldrop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cooldrop.filetransfer.FileTransferViewModel
import com.example.cooldrop.filetransfer.composables.FileTransferScreen
import com.example.cooldrop.nameselect.NameSelectViewModel
import com.example.cooldrop.nameselect.composables.NameSelectScreen
import java.util.UUID

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val fileTransferViewModel: FileTransferViewModel = viewModel()
    val nameSelectViewModel: NameSelectViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = NameSelect.route,
        modifier = modifier
    ) {
        composable(route = FileTransfer.route) {
            FileTransferScreen(fileTransferViewModel = fileTransferViewModel)
        }
        composable(route = NameSelect.route) {
            NameSelectScreen(
                onSubmit = {
                    fileTransferViewModel.setUser(
                        newUser = User(nameSelectViewModel.name, UUID.randomUUID(), UUID.randomUUID())
                    )
                    navController.navigate(FileTransfer.route)
                },
                nameSelectViewModel = nameSelectViewModel
            )
        }
    }
}