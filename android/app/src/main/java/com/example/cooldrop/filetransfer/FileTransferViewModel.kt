package com.example.cooldrop.filetransfer

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooldrop.ConnectionObserver
import com.example.cooldrop.CooldropIOClient
import com.example.cooldrop.DataConnection
import com.example.cooldrop.DataConnectionObserver
import com.example.cooldrop.FileHeader
import com.example.cooldrop.P2PConnection
import com.example.cooldrop.PeerMessage
import com.example.cooldrop.User
import com.example.cooldrop.readFile
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.webrtc.PeerConnectionFactory
import org.webrtc.PeerConnectionFactory.InitializationOptions
import org.webrtc.SessionDescription

class FileTransferViewModel(application: Application) : AndroidViewModel(application) {


    val user: User
        get() = io.user

    private var _peers: MutableList<P2PConnection> = mutableStateListOf()
    val peers: List<P2PConnection>
        get() = _peers

    val launchedEffectKey = 0

    private val io = CooldropIOClient(
        "ws://192.168.0.60:8080",
        _peers,
        CooldropIOObserver (this)
    )

    private class CooldropIOObserver (val vm: FileTransferViewModel) : CooldropIOClient.Observer {

        val observer = object : ConnectionObserver {
            override fun onOpen() {
                println("Opened connection")
            }

            override fun onClose() {
                println("Closed connection")
            }

            override fun onError() {
                TODO("Not yet implemented")
            }

        }

        val dataObserver = object : DataConnectionObserver {
            override fun onReceiveData(byteArray: ByteArray) {
                println("Received data")
            }
        }

        override fun onPeerJoin(peer: PeerInfo, sessionDescription: SessionDescription?) {
            val newPeer = P2PConnection(
                vm.io,
                observer,
                dataObserver,
                PeerConnectionFactory.builder().createPeerConnectionFactory(),
                peer,
                sessionDescription
            )
            vm.addPeer(newPeer)
            newPeer.openConnection()
        }

        override fun onPeerLeave(peerInfo: PeerInfo) {
            vm.removePeer(peerInfo)
        }

        override fun onClose() {
            vm._peers = mutableStateListOf()
        }

    }

    init {
        PeerConnectionFactory.initialize(InitializationOptions.builder(application.applicationContext).createInitializationOptions())
    }

    fun connectToServer() {
        io.connect()
    }

    fun removePeer(peerInfo: PeerInfo) {
        val peerToRemove = _peers.find { it.peerInfo.publicUuid == peerInfo.publicUuid }
        _peers.remove(peerToRemove)
    }

    fun addPeer(peer: P2PConnection) {
        _peers.add(peer)
    }

    fun setName(newName: String) {
        io.user = User(newName, user.publicUuid, user.privateUuid)
    }

    fun onPeerClicked(peer: PeerInfo) {
        println("Peer clicked")
    }

    fun onPeerUris(peer: PeerInfo, uris: List<Uri>) {
        val dataConnection = peers.find({ it.peerInfo == peer }) ?: return
        val contentResolver = getApplication<Application>().applicationContext.contentResolver;
        viewModelScope.launch {
            uris.forEach { uri ->
                readFile(
                    uri,
                    contentResolver,
                    { onHeader(dataConnection, it) },
                    { onChunk(dataConnection, it) }
                )
            }
        }
    }

    private fun onHeader(connection: DataConnection, header: FileHeader) {
        println("Sending ${header}")
        val message = PeerMessage(
            type = "header",
            data = Json.encodeToString(header)
        )
        connection.sendText(Json.encodeToString(message))
    }

    private fun onChunk(connection: DataConnection, byteArray: ByteArray) : Boolean {
        if (!connection.sendData(byteArray)) {
            println("SEND FAIL!!!!!!!!!!!!!!!!")
            return false
        }
        return true
    }
}