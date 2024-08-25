package com.example.cooldrop.filetransfer.connection.server

import com.example.cooldrop.filetransfer.PeerInfo
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

interface SignallingServer {
    fun addObserver(observer: SignallingServerObserver)
    fun removeObserver(observer: SignallingServerObserver)
    fun sendSDPOffer(sessionDescription: SessionDescription, peer: PeerInfo)
    fun sendSDPAnswer(sessionDescription: SessionDescription, peer: PeerInfo)
    fun sendIceCandidate(iceCandidate: IceCandidate, peer: PeerInfo)

    interface Observer {
        fun onSDPOffer(sessionDescription: SessionDescription, peerInfo: PeerInfo)
        fun onSDPAnswer(sessionDescription: SessionDescription, peerInfo: PeerInfo)
        fun onIceCandidate(iceCandidate: IceCandidate, peerInfo: PeerInfo)
    }
}