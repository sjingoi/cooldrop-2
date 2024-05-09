package com.example.cooldrop

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import java.util.UUID

class FileTransferViewModel : ViewModel() {
    private val _peers = getPeers().toMutableStateList()
    val peers: List<Peer>
        get() = _peers

    fun removePeer(peer: Peer) {
        _peers.remove(peer)
    }

    fun addPeer(peer: Peer) {
        _peers.remove(peer)
        _peers.add(peer)
    }
}

private fun getPeers() = List(5) {
    Peer(UUID.randomUUID(), "Seb's Device")
}