package com.example.cooldrop.filetransfer.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooldrop.filetransfer.FileTransferViewModel
import com.example.cooldrop.ui.theme.CooldropTheme

@Composable
fun FileTransferScreen(
    modifier: Modifier = Modifier,
    fileTransferViewModel: FileTransferViewModel = viewModel()
) {
    Column {
        TitleBar {
            Text(text = "Cooldrop", style = MaterialTheme.typography.titleLarge)
        }
        PeerList(list = fileTransferViewModel.peers, onPeerClicked = fileTransferViewModel.onPeerClicked)
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun FileTransferScreenPreview() {
    CooldropTheme {
        FileTransferScreen()
    }
}