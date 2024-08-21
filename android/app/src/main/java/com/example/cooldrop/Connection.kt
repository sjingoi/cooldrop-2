package com.example.cooldrop

interface Connection {

    fun openConnection()

    fun closeConnection()
}

interface ConnectionObserver {
    fun onOpen();
    fun onClose();
    fun onError();
}

interface DataConnection : Connection {
    fun sendData(byteArray: ByteArray) : Boolean
    fun sendText(string: String) : Boolean
}

interface DataConnectionObserver {
    fun onReceiveData(byteArray: ByteArray)
    fun onReceiveText(string: String)
}