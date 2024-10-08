package com.example.cooldrop.filetransfer.connection.peer

import android.content.Context
import com.example.cooldrop.filetransfer.PeerInfo
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription

class WebRTCConnectionFactory(applicationContext: Context) {

    private val peerConnectionFactory: PeerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(applicationContext)
                .createInitializationOptions()
        )
    }

    fun createDataConnection(
        peerInfo: PeerInfo,
        observer: WebRTCConnection.Observer,
        remoteDescription: SessionDescription? = null
    ): WebRTCDataConnection {
        return WebRTCDataConnection(
            peerInfo,
            remoteDescription,
            observer,
            peerConnectionFactory,
        )
    }
}