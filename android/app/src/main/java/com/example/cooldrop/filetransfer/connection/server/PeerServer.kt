package com.example.cooldrop.filetransfer.connection.server

import com.example.cooldrop.filetransfer.PeerInfo
import java.util.UUID

interface PeerServer {
    fun addObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    interface Observer {
        fun onPeerJoin(peerInfo: PeerInfo)
        fun onPeerLeave(uuid: UUID)
    }
}