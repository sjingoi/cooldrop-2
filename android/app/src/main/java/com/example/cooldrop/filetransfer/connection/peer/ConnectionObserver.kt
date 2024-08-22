package com.example.cooldrop.filetransfer.connection.peer

interface ConnectionObserver {
    fun onOpen();
    fun onClose();
    fun onError();
}

