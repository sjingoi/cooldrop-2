package com.example.cooldrop

import com.example.cooldrop.filetransfer.PeerInfo
import io.ktor.util.moveToByteArray
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import java.nio.ByteBuffer

val iceServers: List<IceServer> = listOf(PeerConnection
    .IceServer.builder("stun:stun1.l.google.com:19302").createIceServer())

class P2PConnection (
    protected val signallingServer: SignallingServer,
    protected val observer: ConnectionObserver,
    protected val dataObserver: DataConnectionObserver,
    val peerInfo: PeerInfo,
    val remoteDescription: SessionDescription?,
) : DataConnection {

    protected lateinit var rtcConnection: PeerConnection;
    private var dataChannel: DataChannel? = null;
    var connected: Boolean = false;

    constructor(
        signallingServer: SignallingServer,
        observer: ConnectionObserver,
        dataObserver: DataConnectionObserver,
        peerConnectionFactory: PeerConnectionFactory,
        peerInfo: PeerInfo,
        remoteDescription: SessionDescription?,
        ) : this(signallingServer, observer, dataObserver, peerInfo, remoteDescription) {

        peerConnectionFactory.createPeerConnection(iceServers, peerConnectionObserver)?.let {
            rtcConnection = it
        }
    }

    override fun sendData(byteArray: ByteArray) : Boolean {
        val bytes = ByteBuffer.wrap(byteArray)
        return dataChannel?.send(DataChannel.Buffer(bytes, true)) ?: false
    }

    override fun sendText(string: String): Boolean {
        val bytes = ByteBuffer.wrap(string.toByteArray())
        return dataChannel?.send(DataChannel.Buffer(bytes, false)) ?: false
    }

    override fun openConnection() {
        signallingServer.addObserver(signallingServerObserver)
        if (remoteDescription == null) { // Local
            dataChannel = this.rtcConnection.createDataChannel("channel", DataChannel.Init())
            dataChannel?.registerObserver(dataChannelObserver)
            rtcConnection.createOffer(sdpObserver, MediaConstraints())
        } else { // Remote connection
            rtcConnection.setRemoteDescription(sdpObserver, remoteDescription)
            rtcConnection.createAnswer(sdpObserver, MediaConstraints())
        }
    }

    override fun closeConnection () {
        signallingServer.removeObserver(signallingServerObserver)
        dataChannel?.close()
    }

    private val sdpObserver = object : SdpObserver {
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

        override fun onSetSuccess() {
            println("SDP set successfully!")
        }

        override fun onCreateFailure(p0: String?) { error("Not implemented") }
        override fun onSetFailure(p0: String?) { error("Not implemented") }
    }

    private val peerConnectionObserver = object : PeerConnection.Observer {
        override fun onIceCandidate(ice: IceCandidate?) {
            println("ICE CANDIDATE")
            if (ice != null) {
                signallingServer.sendIceCandidate(ice, peerInfo)
            }
        }

        override fun onDataChannel(dc: DataChannel?) {
            println("DATACHANNEL")
            if (dc != null) {
                dc.registerObserver(dataChannelObserver)
                dataChannel = dc
                observer.onOpen()
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

        override fun onIceConnectionReceivingChange(p0: Boolean) { println ("onIceConnectionReceivingChange") }
        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) { println("onIceConnectionReceivingChange") }
        override fun onAddStream(p0: MediaStream?) { println("onAddStream") }
        override fun onRemoveStream(p0: MediaStream?) { println("onRemoveStream") }
    }

    private val signallingServerObserver = object : SignallingServerObserver {

        override fun onSDPAnswer(sessionDescription: SessionDescription, peerInfo: PeerInfo) {
            if (peerInfo.publicUuid != this@P2PConnection.peerInfo.publicUuid)
                return
            rtcConnection.setRemoteDescription(sdpObserver ,sessionDescription)
        }

        override fun onIceCandidate(iceCandidate: IceCandidate, peerInfo: PeerInfo) {
            if (peerInfo.publicUuid != this@P2PConnection.peerInfo.publicUuid)
                return
            rtcConnection.addIceCandidate(iceCandidate)
        }
    }

    private val dataChannelObserver = object : DataChannel.Observer {

        override fun onBufferedAmountChange(previousAmount: Long) { }

        override fun onStateChange() { }

        override fun onMessage(buffer: DataChannel.Buffer?) {
            buffer?.data?.moveToByteArray()?.let {
                dataObserver.onReceiveData(it)
            }
        }

    }

}

interface P2PConnectionObserver {
    fun onOpen();
    fun onClose();
    fun onError();
}
