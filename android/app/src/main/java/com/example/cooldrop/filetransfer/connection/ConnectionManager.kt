package com.example.cooldrop.filetransfer.connection

import com.example.cooldrop.filetransfer.connection.peer.P2PConnection

abstract class ConnectionManager<T : P2PConnection>(
    protected val observer: Observer<T>
) {

    protected abstract val disconnectedPeers: MutableSet<T>

    protected abstract val connectedPeers: MutableSet<T>

    fun closeAllPeers() {
        disconnectedPeers.forEach { it.closeConnection() }
        connectedPeers.forEach { it.closeConnection() }
    }

    interface Observer<T: P2PConnection> {
        fun onPeerConnected(peer: T)
        fun onPeerDisconnected(peer: T)
        fun onPeerJoin(peer: T)
        fun onPeerLeave(peer: T)
    }
}