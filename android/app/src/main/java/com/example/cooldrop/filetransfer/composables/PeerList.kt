package com.example.cooldrop.filetransfer.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cooldrop.filetransfer.Peer
import com.example.cooldrop.ui.theme.CooldropTheme

@Composable
fun PeerList(
    list: List<Peer>,
    onPeerClicked: (Peer) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = list,
            key = {peer -> peer.publicUuid}
        ) {peer ->
            PeerItem(peer = peer, onClick = { onPeerClicked(peer) })
        }
    }
}

@Preview
@Composable
fun PeerListPreview() {
    CooldropTheme {
        PeerList(list = emptyList(), onPeerClicked = {})
    }
}