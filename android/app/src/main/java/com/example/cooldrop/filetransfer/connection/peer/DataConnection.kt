package com.example.cooldrop.filetransfer.connection.peer

interface DataConnection : Connection {
    fun sendData(byteArray: ByteArray) : Boolean
    fun sendText(string: String) : Boolean
}
