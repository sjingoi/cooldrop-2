package com.example.cooldrop.filetransfer.connection.server

import com.example.cooldrop.filetransfer.PeerInfo
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.util.UUID

interface SignallingServer {
    fun addObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    fun sendSDPOffer(sessionDescription: SessionDescription, peer: PeerInfo)
    fun sendSDPAnswer(sessionDescription: SessionDescription, peer: PeerInfo)
    fun sendIceCandidate(iceCandidate: IceCandidate, peer: PeerInfo)

    interface Observer {
        fun onSDPOffer(sessionDescription: SessionDescription, peerUuid: UUID)
        fun onSDPAnswer(sessionDescription: SessionDescription, peerUuid: UUID)
        fun onIceCandidate(iceCandidate: IceCandidate, peerUuid: UUID)
    }
}