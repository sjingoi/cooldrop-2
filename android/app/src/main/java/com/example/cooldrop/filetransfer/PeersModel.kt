package com.example.cooldrop.filetransfer

import android.net.Uri
import com.example.cooldrop.filetransfer.connection.ConnectionManager
import com.example.cooldrop.filetransfer.connection.WebRTCConnectionManager
import com.example.cooldrop.filetransfer.connection.peer.WebRTCConnection
import com.example.cooldrop.filetransfer.connection.peer.WebRTCConnectionFactory
import com.example.cooldrop.filetransfer.connection.server.CooldropServer
import com.example.cooldrop.filetransfer.connection.server.SignallingServer

abstract class PeersModel (
    webRTCConnectionFactory: WebRTCConnectionFactory
) {
    val peers = mutableListOf<WebRTCConnection>()
    val signallingServer: SignallingServer = CooldropServer("ws://192.168.0.60:8080")
    val connectionManager: WebRTCConnectionManager

    init {
        signallingServer = CooldropServer()
        connectionManager = WebRTCConnectionManager(
            ManagerObserver(),
            signallingServer,
            webRTCConnectionFactory
        )
    }

    fun sendFile(uri: Uri, peer: PeerInfo) {

    }

    inner class ManagerObserver : ConnectionManager.Observer {

    }

    inner class CooldropObserver : CooldropServer.Observer {
        override fun onOpen() {
            TODO("Not yet implemented")
        }

        override fun onClose() {
            TODO("Not yet implemented")
        }

    }
}