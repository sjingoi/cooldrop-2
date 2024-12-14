package com.example.cooldrop.filetransfer.connection.peer

import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

abstract class WebRTCConnection(
    peerConnectionFactory: PeerConnectionFactory,
    observer: Observer,
) : P2PConnection {

    enum class RTCConnectionType {
        UNINITIALIZED, LOCAL, REMOTE
    }

    override var connected = false;
    protected lateinit var rtcConnection: PeerConnection;
    protected var connectionType = RTCConnectionType.UNINITIALIZED
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
                onDataChannelCreated(dc)
            }
        }

        override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
            println("Signalling state changed: $p0")
        }

        override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
            when (state) {
                PeerConnection.IceConnectionState.CONNECTED -> {
                    observer.onOpen()
                    println("Ice connected")
                }
                else -> {}
            }
            println("Ice connection change: $state")
        }

        override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
            println("Ice gathering changed: $p0")
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
            onStreamAdded(stream)
        }

        override fun onRemoveStream(stream: MediaStream) {
            onStreamRemoved(stream)
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
        peerConnectionFactory.createPeerConnection(ICE_SERVERS, peerConnectionObserver)?.let {
            rtcConnection = it
        }
    }

    fun startLocalConnection() {
        assert(connectionType == RTCConnectionType.UNINITIALIZED)

        connectionType = RTCConnectionType.LOCAL;
        rtcConnection.createOffer(sdpObserver, MediaConstraints())
    }

    fun startRemoteConnection(remoteDescription: SessionDescription) {
        assert(connectionType == RTCConnectionType.UNINITIALIZED)

        connectionType = RTCConnectionType.REMOTE;
        rtcConnection.setRemoteDescription(sdpObserver, remoteDescription)
        rtcConnection.createAnswer(sdpObserver, MediaConstraints())
    }

    override fun closeConnection() {
        rtcConnection.close()
    }

    fun addAnswer(sdpAnswer: SessionDescription) {
        this.rtcConnection.setLocalDescription(sdpObserver, sdpAnswer)
    }
    fun addIceCandidate(iceCandidate: IceCandidate) {
        this.rtcConnection.addIceCandidate(iceCandidate)
    }

    abstract fun onDataChannelCreated(dataChannel: DataChannel)
    abstract fun onStreamAdded(mediaStream: MediaStream)
    abstract fun onStreamRemoved(mediaStream: MediaStream)

    interface Observer : P2PConnection.Observer {
        fun onOffer(sdpOffer: SessionDescription)
        fun onAnswer(sdpAnswer: SessionDescription)
        fun onIceCandidate(ice: IceCandidate)
    }
}