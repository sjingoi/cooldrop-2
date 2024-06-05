package com.example.cooldrop.filetransfer

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.cooldrop.CooldropIOClient
import com.example.cooldrop.MessageType
import com.example.cooldrop.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.IceCandidateErrorEvent
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnectionFactory
import org.webrtc.PeerConnectionFactory.InitializationOptions
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import java.util.UUID

@Serializable
data class PeerInfo(
    val peer_uuid: String,
    val peer_name: String
)

@Serializable
data class SDPMessageData(
    val origin_uuid: String,
    val origin_name: String,
    val recipient_uuid: String,
    val sdp: String
)

@Serializable
data class IceCandidateMessageData(
    val origin_uuid: String,
    val recipient_uuid: String,
    val ice: String
)

@Serializable
data class SerializableSDP(
    val sdp: String?,
    val type: String, // SessionDescription.Type canonical form
)

@Serializable
data class SerializableIce(
    val candidate: String,
    val sdpMLineIndex: Int,
    val sdpMid: String,
    val usernameFragment: String? = null
)

val iceServers: List<IceServer> = listOf(PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer())

class FileTransferViewModel(application: Application) : AndroidViewModel(application) {

    private var _user = User("Android Dev Test", UUID( 0 , 0 ), UUID.randomUUID())
    val user: User
        get() = _user

//    private val _peers = getPeers().toMutableStateList()
    private val _peers: MutableList<Peer> = mutableStateListOf()
    val peers: List<Peer>
        get() = _peers

    private val io = CooldropIOClient("ws://192.168.0.60:8080")

    init {
        PeerConnectionFactory.initialize(InitializationOptions.builder(application.applicationContext).createInitializationOptions())
        val peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()

        io.addCallback(MessageType.PRIVATE_UUID_REQ) {
            val info = PeerInfo(UUID.randomUUID().toString(), user.name)
            val message = Json.encodeToString(info)
            io.send(MessageType.PRIVATE_UUID, message)
        }

        io.addCallback(MessageType.PUBLIC_UUID) {publicUuid ->
            _user = User(user.name, UUID.fromString(publicUuid), user.privateUuid)
        }

        io.addCallback(MessageType.SDP_OFFER_REQ) {data ->
            println("Creating new peer")
            lateinit var newPeer: Peer
            val peerInfo: PeerInfo = Json.decodeFromString(data)
            val pcObserver = object : PeerConnection.Observer {
                override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
                    println("Signalling state changed: ${p0}")
                }

                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                    println("Ice connection change: ${p0}")
                }

                override fun onIceConnectionReceivingChange(p0: Boolean) {
                    TODO("Not yet implemented")
                }

                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
                    println("Ice gathering changed: ${p0}")
                }

                override fun onIceCandidate(ice: IceCandidate?) {
                    println("ICE CANDIDATE: ")
                    println(ice)
                    println("PUBLIC UUID: ${user.publicUuid}")
                    if (ice != null) {
                        val iceCandidateMessage = IceCandidateMessageData(
                            origin_uuid = user.publicUuid.toString(),
                            recipient_uuid = peerInfo.peer_uuid,
                            ice = Json.encodeToString(SerializableIce(ice.sdp, ice.sdpMLineIndex, ice.sdpMid))
                        )
                        io.send(MessageType.ICE_CANDIDATE, Json.encodeToString(iceCandidateMessage))
                    }
                }

                override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
                    TODO("Not yet implemented")
                }

                override fun onAddStream(p0: MediaStream?) {
                    TODO("Not yet implemented")
                }

                override fun onRemoveStream(p0: MediaStream?) {
                    TODO("Not yet implemented")
                }

                override fun onDataChannel(p0: DataChannel?) {
                    TODO("Not yet implemented")
                }

                override fun onRenegotiationNeeded() {
                    println("Renegotiation needed")
                }

            }
            val sdpObserver = object : SdpObserver {
                override fun onCreateSuccess(sdp: SessionDescription?) {
                    println("Created offer")
                    if (sdp != null) {
                        newPeer.rtcConnection.setLocalDescription(this, sdp)
                        val sdpMessageData = SDPMessageData(
                            origin_name = user.name,
                            origin_uuid = user.publicUuid.toString(),
                            recipient_uuid = peerInfo.peer_uuid,
                            sdp = Json.encodeToString(SerializableSDP(sdp.description, sdp.type.canonicalForm()))
                        )
                        io.send(MessageType.SDP_OFFER, Json.encodeToString(sdpMessageData))
//                        println(p0.type)
//                        println(p0.description)
                    }
                }

                override fun onSetSuccess() {
                    println("SDP set successfully!")
                }

                override fun onCreateFailure(p0: String?) {
                    TODO("Not yet implemented")
                }

                override fun onSetFailure(p0: String?) {
                    TODO("Not yet implemented")
                }
            }
            val rtcConnection = peerConnectionFactory
                .createPeerConnection(iceServers, pcObserver) ?: return@addCallback
            val dataChannel = rtcConnection.createDataChannel("channel", DataChannel.Init())
            newPeer = Peer(
                name = peerInfo.peer_name,
                publicUuid = UUID.fromString(peerInfo.peer_uuid),
                rtcConnection = rtcConnection,
                dataChannel = dataChannel
            )
            newPeer.rtcConnection.createOffer(sdpObserver, MediaConstraints())
            this.addPeer(newPeer)
        }

        io.addCallback(MessageType.SDP_ANSWER) {data ->
            println("Recieved SDP Answer")
            val sdpMessageData = Json.decodeFromString<SDPMessageData>(data)
            val serializableSDP = Json.decodeFromString<SerializableSDP>(sdpMessageData.sdp)
            assert(serializableSDP.type == SessionDescription.Type.ANSWER.canonicalForm())
            val sdp = SessionDescription(SessionDescription.Type.ANSWER, serializableSDP.sdp)
            val originPeer = this.peers.find { peer: Peer -> peer.publicUuid.toString() == sdpMessageData.origin_uuid }
            val sdpObserver = object : SdpObserver {
                override fun onCreateSuccess(sdp: SessionDescription?) {
                    TODO("Not yet implemented")
                }

                override fun onSetSuccess() {
                    println("SDP set successfully!")
                }

                override fun onCreateFailure(p0: String?) {
                    TODO("Not yet implemented")
                }

                override fun onSetFailure(p0: String?) {
                    TODO("Not yet implemented")
                }
            }
            if (originPeer == null) {
                println("Could not find recipient for sdp addressed to ${sdpMessageData.origin_uuid}")
                println(this.peers)
                return@addCallback
            }
            originPeer.rtcConnection.setRemoteDescription(sdpObserver, sdp)
        }

        io.addCallback(MessageType.ICE_CANDIDATE) {data ->
            println("Recieved ice candidate")
            val ice = Json.decodeFromString<IceCandidateMessageData>(data)
            val serializableIce = Json.decodeFromString<SerializableIce>(ice.ice)
            println(serializableIce)
            val iceCandidate = IceCandidate(serializableIce.sdpMid, serializableIce.sdpMLineIndex, serializableIce.candidate)
            val originPeer = this.peers.find { peer: Peer ->
                peer.publicUuid.toString() == ice.origin_uuid
            }
            if (originPeer == null) {
                println("Could not find recipient for ice candidate addressed to ${ice.origin_uuid}")
                return@addCallback
            }
            originPeer.rtcConnection.addIceCandidate(iceCandidate)
        }

        io.addCallback(MessageType.SDP_OFFER) {data ->
            lateinit var newPeer: Peer
            val sdpMessageData = Json.decodeFromString<SDPMessageData>(data)
            val serializableSDP = Json.decodeFromString<SerializableSDP>(sdpMessageData.sdp)
            assert(serializableSDP.type == SessionDescription.Type.OFFER.canonicalForm())
            val sdp = SessionDescription(SessionDescription.Type.OFFER, serializableSDP.sdp)
            println("SDP Object:")
            println(sdp.type)
            val pcObserver = object : PeerConnection.Observer {
                override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
                    println("Signalling state changed: ${p0}")
                }

                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                    println("Ice connection change: ${p0}")
                }

                override fun onIceConnectionReceivingChange(p0: Boolean) {
                    TODO("Not yet implemented")
                }

                override fun onIceCandidateError(event: IceCandidateErrorEvent?) {
                    println("ICE CANDIDATE ERROR: ")
                    if (event != null) {
                        println(event.errorText)
                    }
                    super.onIceCandidateError(event)
                }



                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
                    println("Ice gathering changed: ${p0}")
                }

                override fun onIceCandidate(ice: IceCandidate?) {
                    println("ICE CANDIDATE: ")
                    println(ice)
                    println("PUBLIC UUID: ${user.publicUuid}")
                    if (ice != null) {
                        val iceCandidateMessage = IceCandidateMessageData(
                            origin_uuid = user.publicUuid.toString(),
                            recipient_uuid = sdpMessageData.origin_uuid,
                            ice = Json.encodeToString(SerializableIce(ice.sdp, ice.sdpMLineIndex, ice.sdpMid))
                        )
                        io.send(MessageType.ICE_CANDIDATE, Json.encodeToString(iceCandidateMessage))
                    }
                }

                override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
                    TODO("Not yet implemented")
                }

                override fun onAddStream(p0: MediaStream?) {
                    TODO("Not yet implemented")
                }

                override fun onRemoveStream(p0: MediaStream?) {
                    TODO("Not yet implemented")
                }

                override fun onDataChannel(p0: DataChannel?) {
                    println("DATA CHANNEL!")
                }

                override fun onRenegotiationNeeded() {
                    println("Renegotiation needed")
                }
            }
            val sdpObserver = object : SdpObserver {
                override fun onCreateSuccess(sdp: SessionDescription?) {
                    println("Created SDP")
                    if (sdp != null) {
                        assert(sdp.type == SessionDescription.Type.ANSWER)
                        val serializableSDPtoSend = SerializableSDP(
                            sdp = sdp.description,
                            type = sdp.type.canonicalForm()
                        )
                        val sdpMessageDatatoSend = SDPMessageData(
                            origin_name = user.name,
                            origin_uuid = user.publicUuid.toString(),
                            recipient_uuid = sdpMessageData.origin_uuid,
                            sdp = Json.encodeToString(serializableSDPtoSend)
                        )
                        println("Sending sdp answer")
                        println(sdpMessageDatatoSend)
                        newPeer.rtcConnection.setLocalDescription(this, sdp)
                        io.send(MessageType.SDP_ANSWER, Json.encodeToString(sdpMessageDatatoSend))
                    }
                }

                override fun onSetSuccess() {
                    println("SDP set successfully!")
                }

                override fun onCreateFailure(p0: String?) {
                    TODO("Not yet implemented")
                }

                override fun onSetFailure(p0: String?) {
                    TODO("Not yet implemented")
                }
            }
            val rtcConnection = peerConnectionFactory
                .createPeerConnection(iceServers, pcObserver) ?: return@addCallback

            newPeer = Peer(
                name = sdpMessageData.origin_name,
                publicUuid = UUID.fromString(sdpMessageData.origin_uuid),
                rtcConnection = rtcConnection
            )

            rtcConnection.setRemoteDescription(sdpObserver, sdp)
            rtcConnection.createAnswer(sdpObserver, MediaConstraints())

            this.addPeer(newPeer)
        }
    }

    fun removePeer(peer: Peer) {
        _peers.remove(peer)
    }

    fun addPeer(peer: Peer) {
        _peers.add(peer)
    }

    fun setName(newName: String) {
        _user = User(newName, user.publicUuid, user.privateUuid)
    }

    val onPeerClicked: (Peer) -> Unit = {peer ->
        println("Peer clicked")
    }
}

//private fun getPeers() = List(5) {
//    Peer(UUID.randomUUID(), "Seb's Device")
//}