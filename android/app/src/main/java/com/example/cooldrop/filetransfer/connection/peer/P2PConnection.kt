package com.example.cooldrop.filetransfer.connection.peer

interface P2PConnection {
    val connected: Boolean
    fun openConnection()
    fun closeConnection()

    interface Observer {
        fun onOpen();
        fun onClose();
        fun onError(string: String);
    }
}
//
//abstract class P2PConnectionImpl (
//    override val peerInfo: PeerInfo,
//    protected val observer: P2PConnection.Observer,
//) : P2PConnection {
//    override val connected = false;
//    abstract override fun openConnection()
//    abstract override fun closeConnection()
//}
