package com.example.cooldrop.filetransfer.connection.server

import com.example.cooldrop.filetransfer.PeerInfo
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription


interface SignallingServerObserver {
    fun onSDPOffer(sessionDescription: SessionDescription, peerInfo: PeerInfo) {}
    fun onSDPAnswer(sessionDescription: SessionDescription, peerInfo: PeerInfo) {}
    fun onIceCandidate(iceCandidate: IceCandidate, peerInfo: PeerInfo) {}
}