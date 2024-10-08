package com.example.cooldrop.filetransfer.connection

import com.example.cooldrop.filetransfer.PeerInfo
import com.example.cooldrop.filetransfer.connection.peer.P2PConnection
import com.example.cooldrop.filetransfer.connection.peer.WebRTCConnection
import com.example.cooldrop.filetransfer.connection.peer.WebRTCConnectionFactory
import com.example.cooldrop.filetransfer.connection.server.SignallingServer
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class WebRTCConnectionManager(
    observer: Observer<WebRTCConnection>,
    val signallingServer: SignallingServer,
    webRTCConnectionFactory: WebRTCConnectionFactory,
) : ConnectionManager<WebRTCConnection>(observer) {

    private val signallingServerObserver: SignallingServer.Observer

    init {
        signallingServerObserver = object : SignallingServer.Observer {
            override fun onSDPOffer(sessionDescription: SessionDescription, peerInfo: PeerInfo) {
                lateinit var newPeer: WebRTCConnection
                val connectionObserver =
                    ConnectionObserver(newPeer, signallingServer, disconnectedPeers, connectedPeers)
                newPeer = webRTCConnectionFactory.createDataConnection(
                    peerInfo,
                    connectionObserver,
                    sessionDescription,
                )
                disconnectedPeers.add(newPeer)
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

    private class ConnectionObserver(
        val connection: WebRTCConnection,
        val signallingServer: SignallingServer,
        val disconnectedPeers: MutableSet<WebRTCConnection>,
        val connectedPeers: MutableSet<WebRTCConnection>
    ) : P2PConnection.Observer,
        WebRTCConnection.Observer {

        override fun onOpen() {
            disconnectedPeers.add(connection)
        }

        override fun onClose() {
            disconnectedPeers.remove(connection)
            connectedPeers.remove(connection)
        }

        override fun onOffer(sdpOffer: SessionDescription) {
            signallingServer.sendSDPOffer(sdpOffer, connection.peerInfo)
        }

        override fun onAnswer(sdpAnswer: SessionDescription) {
            signallingServer.sendSDPAnswer(sdpAnswer, connection.peerInfo)
        }

        override fun onIceCandidate(ice: IceCandidate) {
            signallingServer.sendIceCandidate(ice, connection.peerInfo)
        }

        override fun onError(errorStr: String) {
            println("Error: ${errorStr}")
        }

    }

}