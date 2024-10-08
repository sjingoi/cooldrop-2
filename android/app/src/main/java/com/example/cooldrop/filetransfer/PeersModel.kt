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
    val connectionManager: WebRTCConnectionManager
    val signallingServer: SignallingServer

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

    private class ManagerObserver : ConnectionManager.Observer<WebRTCConnection> {
        override fun onPeerConnected(peer: WebRTCConnection) {
            TODO("Not yet implemented")
        }

        override fun onPeerDisconnected(peer: WebRTCConnection) {
            TODO("Not yet implemented")
        }

        override fun onPeerJoin(peer: WebRTCConnection) {
            TODO("Not yet implemented")
        }

        override fun onPeerLeave(peer: WebRTCConnection) {
            TODO("Not yet implemented")
        }

    }

    private class
}