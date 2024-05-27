package com.example.cooldrop.filetransfer

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.cooldrop.CooldropIOClient
import com.example.cooldrop.MessageType
import com.example.cooldrop.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class PeerInfo(
    val peerUuid: String,
    val peerName: String
)

data class SDPMessageData(
    val originUuid: String,
    val originName: String,
    val recipientUuid: String,
    val sdp: String
)

data class IceCandidateMessageData(
    val originUuid: String,
    val recipientUuid: String,
    val ice: String
)

class FileTransferViewModel : ViewModel() {

    private var _user = User("", UUID.randomUUID(), UUID.randomUUID())
    val user: User
        get() = _user

    private val _peers = getPeers().toMutableStateList()
    val peers: List<Peer>
        get() = _peers

    private val io = CooldropIOClient("ws://192.168.0.60:8080")

    init {
        io.addCallback(MessageType.PRIVATE_UUID_REQ) {
            val info = PeerInfo(UUID.randomUUID().toString(), "Android Dev Test")
            io.send(MessageType.PRIVATE_UUID, Json.encodeToString(info))
        }
    }

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