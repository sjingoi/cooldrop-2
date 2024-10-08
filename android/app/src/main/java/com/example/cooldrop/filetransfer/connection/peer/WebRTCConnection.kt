package com.example.cooldrop.filetransfer.connection.peer

import com.example.cooldrop.filetransfer.PeerInfo
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

abstract class WebRTCConnection(
    peerInfo: PeerInfo,
    private val remoteDescription: SessionDescription?,
    observer: Observer,
    private val peerConnectionFactory: PeerConnectionFactory,
) : P2PConnection(peerInfo, observer) {

    protected lateinit var rtcConnection: PeerConnection;

    protected val remoteConnection: Boolean = remoteDescription == null;

    companion object {
        val ICE_SERVERS: List<PeerConnection.IceServer> = listOf(
            PeerConnection
                .IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
        )
    }

    private val peerConnectionObserver = object : PeerConnection.Observer {
        override fun onIceCandidate(ice: IceCandidate?) {
            println("ICE CANDIDATE")
            if (ice != null) {
                observer.onIceCandidate(ice)
            }
        }

        override fun onDataChannel(dc: DataChannel?) {
            println("DATACHANNEL")
            if (dc != null) {
                this.onDataChannel(dc)
            }
        }

        override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
            println("Signalling state changed: ${p0}")
        }

        override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
            when (state) {
                PeerConnection.IceConnectionState.CONNECTED -> {
//                    observer.onOpen()
                    println("Ice connected")
                }
                else -> {}
            }
            println("Ice connection change: $state")
        }

        override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
            println("Ice gathering changed: ${p0}")
        }

        override fun onRenegotiationNeeded() {
            println("Renegotiation needed")
        }

        override fun onIceConnectionReceivingChange(p0: Boolean) {
            println("onIceConnectionReceivingChange")
        }

        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
            println("onIceConnectionReceivingChange")
        }

        override fun onAddStream(stream: MediaStream) {
            onAddStream(stream)
        }

        override fun onRemoveStream(stream: MediaStream) {
            onAddStream(stream)
        }
    }

    private val sdpObserver = object : SdpObserver {
        override fun onCreateSuccess(sdp: SessionDescription?) {
            println("CREATED SDP")
            if (sdp != null) {
                rtcConnection.setLocalDescription(this, sdp)
                when (sdp.type) {
                    SessionDescription.Type.OFFER -> {
                        observer.onOffer(sdp)
                    }
                    SessionDescription.Type.ANSWER -> {
                        observer.onAnswer(sdp)
                    }
                    else -> {
                        println("Unhandled sdp type ${sdp.type}")
                    }
                }
            }
        }

        override fun onSetSuccess() {
            println("SDP set successfully!")
        }

        override fun onCreateFailure(errorStr: String) {
            observer.onError(errorStr)
        }

        override fun onSetFailure(errorStr: String) {
            observer.onError(errorStr)
        }
    }

    init {
        peerConnectionFactory.createPeerConnection(P2PConnectionOld.ICE_SERVERS, peerConnectionObserver)?.let {
            rtcConnection = it
        }
    }

    override fun openConnection() {
        if (remoteDescription == null) {
            rtcConnection.createOffer(sdpObserver, MediaConstraints())
        } else {
            rtcConnection.setRemoteDescription(sdpObserver, remoteDescription)
            rtcConnection.createAnswer(sdpObserver, MediaConstraints())
        }
    }

    override val connected: Boolean
        get() = TODO("Not yet implemented")

    fun addAnswer(sdpAnswer: SessionDescription) {
        this.rtcConnection.setLocalDescription(sdpObserver, sdpAnswer)
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        this.rtcConnection.addIceCandidate(iceCandidate)
    }

    protected open fun onDataChannel(dataChannel: DataChannel) { }

    protected open fun onAddStream(mediaStream: MediaStream) { }

    protected open fun onRemoveStream(mediaStream: MediaStream) { }

    public interface Observer : P2PConnection.Observer {
        fun onOffer(sdpOffer: SessionDescription)
        fun onAnswer(sdpAnswer: SessionDescription)
        fun onIceCandidate(ice: IceCandidate)
    }
}