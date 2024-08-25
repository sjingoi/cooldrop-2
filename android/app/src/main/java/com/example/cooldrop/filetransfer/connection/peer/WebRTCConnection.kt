package com.example.cooldrop.filetransfer.connection.peer

import com.example.cooldrop.filetransfer.PeerInfo
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

abstract class WebRTCConnection(
    peerInfo: PeerInfo,
    observer: P2PConnection.Observer,
    protected val rtcObserver: Observer
) : P2PConnection(peerInfo, observer) {

    override val connected: Boolean
        get() = TODO("Not yet implemented")

    fun addAnswer(sdpAnswer: SessionDescription) {
        TODO("Not yet implemented")
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        TODO("Not yet implemented")
    }

    public interface Observer {
        fun onOffer(sdpOffer: SessionDescription)
        fun onAnswer(sdpAnswer: SessionDescription)
        fun onIceCandidate(ice: IceCandidate)
        fun onError()
    }
}