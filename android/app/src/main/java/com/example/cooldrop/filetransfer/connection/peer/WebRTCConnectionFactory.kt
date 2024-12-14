package com.example.cooldrop.filetransfer.connection.peer

import android.content.Context
import org.webrtc.PeerConnectionFactory

class WebRTCConnectionFactory(applicationContext: Context) {

    private val peerConnectionFactory: PeerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(applicationContext)
                .createInitializationOptions()
        )
    }

    fun createDataConnection(
        observer: WebRTCConnection.Observer
    ): WebRTCDataConnection {
        return WebRTCDataConnection(
            observer,
            peerConnectionFactory,
        )
    }
}