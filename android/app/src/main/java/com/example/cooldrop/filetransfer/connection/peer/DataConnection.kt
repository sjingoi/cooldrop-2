package com.example.cooldrop.filetransfer.connection.peer

interface DataConnection : P2PConnection {
    fun sendData(byteArray: ByteArray): Boolean
    fun sendText(string: String): Boolean
}
