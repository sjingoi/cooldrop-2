package com.example.cooldrop.filetransfer.connection.peer

interface DataConnection {
    fun sendData(byteArray: ByteArray): Boolean
    fun sendText(string: String): Boolean
    fun registerObserver(observer: Observer)
    interface Observer {
        fun onReceiveData(byteArray: ByteArray)
        fun onReceiveText(string: String)
    }
}
