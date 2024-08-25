package com.example.cooldrop.filetransfer.connection

import com.example.cooldrop.filetransfer.PeerInfo
import com.example.cooldrop.filetransfer.connection.peer.WebRTCConnection
import com.example.cooldrop.filetransfer.connection.server.SignallingServer
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class WebRTCConnectionManager(
    observer: Observer<WebRTCConnection>,
    signallingServer: SignallingServer,
) : ConnectionManager<WebRTCConnection>(observer) {

    private val signallingServerObserver: SignallingServer.Observer

    init {
        signallingServerObserver = object : SignallingServer.Observer {
            override fun onSDPOffer(sessionDescription: SessionDescription, peerInfo: PeerInfo) {

            }

            override fun onSDPAnswer(sessionDescription: SessionDescription, peerInfo: PeerInfo) {
                disconnectedPeers.find { it.peerInfo == peerInfo }?.addAnswer(sessionDescription)
                    ?: {
                        println("Could not find peer ${peerInfo.publicUuid} for answer.")
                    }
            }

            override fun onIceCandidate(iceCandidate: IceCandidate, peerInfo: PeerInfo) {
                disconnectedPeers.find { it.peerInfo == peerInfo }?.addIceCandidate(iceCandidate)
                    ?: {
                        println("Could not find peer ${peerInfo.publicUuid} for ice.")
                    }
            }

        }
    }

    override val disconnectedPeers: MutableSet<WebRTCConnection> = mutableSetOf()

    override val connectedPeers: MutableSet<WebRTCConnection> = mutableSetOf()


}