package com.example.cooldrop.filetransfer.connection.peer

import com.example.cooldrop.filetransfer.PeerInfo

abstract class P2PConnection (
    val peerInfo: PeerInfo,
    protected open val observer: Observer,
) {

    abstract val connected: Boolean

    abstract fun openConnection()

    abstract fun closeConnection()

    interface Observer {
        fun onOpen();
        fun onClose();
        fun onError(string: String);
    }
}
