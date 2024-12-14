package com.example.cooldrop.filetransfer.connection

import com.example.cooldrop.filetransfer.PeerClass

abstract class ConnectionManager(
    protected val observer: Observer
) {

    protected abstract val disconnectedPeers: MutableSet<PeerClass>
    protected abstract val connectedPeers: MutableSet<PeerClass>

    fun closeAllPeers() {
        disconnectedPeers.forEach { it.connection?.closeConnection() }
        connectedPeers.forEach { it.connection?.closeConnection() }
    }

    interface Observer {
        fun onPeerConnected(peer: PeerClass)
        fun onPeerDisconnected(peer: PeerClass)
        fun onPeerJoin(peer: PeerClass)
        fun onPeerLeave(peer: PeerClass)
    }
}