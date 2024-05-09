package com.example.cooldrop

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooldrop.ui.theme.CooldropTheme

@Composable
fun FileTransferScreen(
    modifier: Modifier = Modifier,
    fileTransferViewModel: FileTransferViewModel = viewModel()
) {
    Column {
        PeerList(list = fileTransferViewModel.peers, onPeerClicked = {})
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun FileTransferScreenPreview() {
    CooldropTheme {
        FileTransferScreen()
    }
}