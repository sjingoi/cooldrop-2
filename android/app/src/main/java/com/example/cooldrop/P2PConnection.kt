package com.example.cooldrop

import com.example.cooldrop.filetransfer.PeerInfo
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

val iceServers: List<IceServer> = listOf(PeerConnection
    .IceServer.builder("stun:stun1.l.google.com:19302").createIceServer())

class P2PConnection (
    protected val signallingServer: SignallingServer,
    protected val observer: P2PConnectionObserver,
    val peerInfo: PeerInfo,
) : SdpObserver, PeerConnection.Observer {
    protected lateinit var rtcConnection: PeerConnection;
    protected var dataChannel: DataChannel? = null;
    var connected: Boolean = false;

    constructor(
        signallingServer: SignallingServer,
        observer: P2PConnectionObserver,
        peerConnectionFactory: PeerConnectionFactory,
        peer: PeerInfo,
        remoteDescription: SessionDescription?) : this(signallingServer, observer, peer
    ) {

        peerConnectionFactory.createPeerConnection(iceServers, this)?.let {
            rtcConnection = it
        }

        if (remoteDescription == null) { // Local connection
            rtcConnection.createOffer(this, MediaConstraints())
        } else { // Remote connection
            println("REMOTE: ${remoteDescription}")
            rtcConnection.setRemoteDescription(this, remoteDescription)
            rtcConnection.createAnswer(this, MediaConstraints())
        }
    }

    override fun onIceCandidate(ice: IceCandidate?) {
        println("ICE CANDIDATE")
        if (ice != null) {
            signallingServer.sendIceCandidate(ice, peerInfo)
        }
    }

    override fun onDataChannel(dc: DataChannel?) {
        if (dataChannel != null) {
            dataChannel = dc
            observer.onOpen()
        }
    }

    override fun onCreateSuccess(sdp: SessionDescription?) {
        println("CREATED SDP")
        if (sdp != null) {
            rtcConnection.setLocalDescription(this, sdp)
            when (sdp.type) {
                SessionDescription.Type.OFFER -> {
                    signallingServer.sendSDPOffer(sdp, peerInfo)
                }
                SessionDescription.Type.ANSWER -> {
                    signallingServer.sendSDPAnswer(sdp, peerInfo)
                }
                SessionDescription.Type.PRANSWER -> {}
                SessionDescription.Type.ROLLBACK -> {}
                null -> {}
            }

        }
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        println("Signalling state changed: ${p0}")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        println("Ice connection change: ${p0}")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        println("Ice gathering changed: ${p0}")
    }

    override fun onRenegotiationNeeded() {
        println("Renegotiation needed")
    }

    override fun onSetSuccess() {
        println("SDP set successfully!")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) { println ("onIceConnectionReceivingChange") }
    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) { println("onIceConnectionReceivingChange") }
    override fun onAddStream(p0: MediaStream?) { println("onAddStream") }
    override fun onRemoveStream(p0: MediaStream?) { println("onRemoveStream") }
    override fun onCreateFailure(p0: String?) { println("onCreateFailure") }
    override fun onSetFailure(p0: String?) { println("onSetFailure") }
}

interface P2PConnectionObserver {
    fun onOpen();
    fun onClose();
    fun onError();
}
