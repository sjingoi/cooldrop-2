package com.example.cooldrop.filetransfer

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.cooldrop.User
import java.util.UUID

class FileTransferViewModel : ViewModel() {

    private var _user = User("", UUID.randomUUID(), UUID.randomUUID())
    val user: User
        get() = _user

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

    fun setUser(newUser: User) {
        _user = newUser
    }

    val onPeerClicked: (Peer) -> Unit = {peer ->
        println("Peer clicked")
    }
}

private fun getPeers() = List(5) {
    Peer(UUID.randomUUID(), "Seb's Device")
}