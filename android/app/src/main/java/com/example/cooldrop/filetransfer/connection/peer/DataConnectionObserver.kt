package com.example.cooldrop.filetransfer.connection.peer

interface DataConnectionObserver {
    fun onReceiveData(byteArray: ByteArray)
    fun onReceiveText(string: String)
}