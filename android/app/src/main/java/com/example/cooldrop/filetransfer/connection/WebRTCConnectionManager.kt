package com.example.cooldrop.filetransfer.connection

import com.example.cooldrop.filetransfer.PeerClass
import com.example.cooldrop.filetransfer.PeerInfo
import com.example.cooldrop.filetransfer.connection.peer.P2PConnection
import com.example.cooldrop.filetransfer.connection.peer.WebRTCConnection
import com.example.cooldrop.filetransfer.connection.peer.WebRTCConnectionFactory
import com.example.cooldrop.filetransfer.connection.server.PeerServer
import com.example.cooldrop.filetransfer.connection.server.SignallingServer
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.util.UUID

class WebRTCConnectionManager(
    observer: ConnectionManager.Observer,
    val peerServer: PeerServer,
    val signallingServer: SignallingServer,
    val webRTCConnectionFactory: WebRTCConnectionFactory,
) : ConnectionManager(observer) {

    override val disconnectedPeers: MutableSet<PeerClass> = mutableSetOf()
    override val connectedPeers: MutableSet<PeerClass> = mutableSetOf()

    init {
        peerServer.addObserver(PeerServerObserver())
        signallingServer.addObserver(SignallingServerObserver())
    }

    inner class ConnectionObserver(val peer: PeerClass) : P2PConnection.Observer,
        WebRTCConnection.Observer {

        override fun onOpen() {
            disconnectedPeers.remove(peer)
            connectedPeers.add(peer)
            observer.onPeerConnected(peer)
        }

        override fun onClose() {
            disconnectedPeers.add(peer)
            connectedPeers.remove(peer)
            observer.onPeerDisconnected(peer)
        }

        override fun onOffer(sdpOffer: SessionDescription) {
            signallingServer.sendSDPOffer(sdpOffer, peer.peerInfo)
        }

        override fun onAnswer(sdpAnswer: SessionDescription) {
            signallingServer.sendSDPAnswer(sdpAnswer, peer.peerInfo)
        }

        override fun onIceCandidate(ice: IceCandidate) {
            signallingServer.sendIceCandidate(ice, peer.peerInfo)
        }

        override fun onError(string: String) {
            println("Error: $string")
        }

    }

    inner class PeerServerObserver : PeerServer.Observer {
        override fun onPeerJoin(peerInfo: PeerInfo) {
            val peer = PeerClass(peerInfo.publicUuid)
            peer.name = peerInfo.name
            peer.connection = webRTCConnectionFactory.createDataConnection(ConnectionObserver(peer))
            disconnectedPeers.add(peer)
            observer.onPeerJoin(peer)
        }

        override fun onPeerLeave(uuid: UUID) {
            val peer = (connectedPeers union disconnectedPeers).find { it.publicUuid == uuid } ?: return
            peer.connection?.closeConnection()
            disconnectedPeers.remove(peer)
            connectedPeers.remove(peer)
            observer.onPeerLeave(peer)
        }
    }

    inner class SignallingServerObserver : SignallingServer.Observer {
        override fun onSDPOfferReq(peerUuid: UUID) {
            val peer = disconnectedPeers.find { it.publicUuid == peerUuid }
            if (peer == null) {
                println("Could not find peer $peerUuid for sdp offer req.")
                return
            }
            val connection = peer.connection ?: return

            if (connection is WebRTCConnection) {
                connection.startLocalConnection()
            }
        }

        override fun onSDPOffer(sessionDescription: SessionDescription, peerUuid: UUID) {

            val peer = disconnectedPeers.find { it.publicUuid == peerUuid }
            if (peer == null) {
                println("Could not find peer $peerUuid for sdp offer.")
                return
            }
            val connection = peer.connection ?: return

            if (connection is WebRTCConnection) {
                connection.startRemoteConnection(sessionDescription)
            }
        }

        override fun onSDPAnswer(sessionDescription: SessionDescription, peerUuid: UUID) {
            val peer = disconnectedPeers.find { it.publicUuid == peerUuid }
            if (peer == null) {
                println("Could not find peer $peerUuid for answer.")
                return
            }
            val connection = peer.connection ?: return

            if (connection is WebRTCConnection) {
                connection.addAnswer(sessionDescription)
            }
        }

        override fun onIceCandidate(iceCandidate: IceCandidate, peerUuid: UUID) {
            val peer = disconnectedPeers.find { it.publicUuid == peerUuid }
            if (peer == null) {
                println("Could not find peer $peerUuid for ice.")
                return
            }
            val connection = peer.connection ?: return

            if (connection is WebRTCConnection) {
                connection.addIceCandidate(iceCandidate)
            }
        }
    }

}