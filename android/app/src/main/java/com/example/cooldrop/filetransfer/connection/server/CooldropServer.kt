package com.example.cooldrop.filetransfer.connection.server

import com.example.cooldrop.User
import com.example.cooldrop.filetransfer.PeerInfo
import com.example.cooldrop.filetransfer.connection.decodeIce
import com.example.cooldrop.filetransfer.connection.decodeSdp
import com.example.cooldrop.filetransfer.connection.encodeIce
import com.example.cooldrop.filetransfer.connection.encodeSdp
import com.example.cooldrop.filetransfer.connection.server.messagedata.IceCandidateMessageData
import com.example.cooldrop.filetransfer.connection.server.messagedata.PeerInfoMessageData
import com.example.cooldrop.filetransfer.connection.server.messagedata.SDPMessageData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.util.UUID

class CooldropServer(
    private val url: String,
    private val observer: Observer
) : SignallingServer, PeerServer {

    var user = User("", UUID(0, 0), UUID.randomUUID())

    private val signallingServerObservers: MutableSet<SignallingServer.Observer> = HashSet()

    private val peerServerObservers: MutableSet<PeerServer.Observer> = HashSet()

    private var client: OkHttpClient = OkHttpClient()

    private val request: Request = Request.Builder().url(url).build()

    private var reconnectOnClose: Boolean = true

    private lateinit var webSocket: WebSocket

    private object MessageType {
        const val TEST = "test"
        const val PRIVATE_UUID = "private-uuid"
        const val PUBLIC_UUID = "public-uuid"
        const val PRIVATE_UUID_REQ = "private-uuid-req"
        const val SDP_OFFER = "sdp-offer"
        const val SDP_ANSWER = "sdp-answer"
        const val SDP_OFFER_REQ = "sdp-offer-req"
        const val ICE_CANDIDATE = "ice-candidate"
        const val PEER_JOIN = "peer-join"
        const val PEER_DISCONNECT = "peer-disconnect"
    }

    private fun initSocket() {
        client = OkHttpClient()
        webSocket = client.newWebSocket(request, WSObserver(this))
        client.dispatcher.executorService.shutdown()
    }


    fun connect() {
        if (::webSocket.isInitialized) webSocket.cancel()
        reconnectOnClose = true
        initSocket()
    }

    fun reconnect() {
        initSocket()
    }

    fun disconnect() {
        if (::webSocket.isInitialized) webSocket.close(1000, "Connection closed by client")
        reconnectOnClose = false
    }

    private fun send(type: String, data: String) {
        val cooldropIOMessage = CooldropIOMessage(type, data)
        val strMessage = Json.encodeToString(cooldropIOMessage)
        println("Sending $type")
        this.webSocket.send(strMessage)
    }

    private fun handleMessage(message: CooldropIOMessage) {
        println("Recieved type ${message.type}")
        when (message.type) {
            MessageType.PUBLIC_UUID -> {
                user.publicUuid = UUID.fromString(message.data)
            }

            MessageType.PRIVATE_UUID_REQ -> {
                val info = PeerInfoMessageData(UUID.randomUUID().toString(), user.name)
                val infoMsg = Json.encodeToString(info)
                send(MessageType.PRIVATE_UUID, infoMsg)
            }

            MessageType.ICE_CANDIDATE -> {
                val iceMsg = Json.decodeFromString<IceCandidateMessageData>(message.data)
                val iceCandidate = decodeIce(iceMsg.ice)
                signallingServerObservers.forEach() { observer ->
                    observer.onIceCandidate(iceCandidate, UUID.fromString(iceMsg.origin_uuid))
                }
            }

            MessageType.SDP_OFFER -> {
                val sdpMsg = Json.decodeFromString<SDPMessageData>(message.data)
                val sessionDescription = decodeSdp(sdpMsg.sdp);
                assert(sessionDescription.type.canonicalForm() == SessionDescription.Type.OFFER.canonicalForm())
                signallingServerObservers.forEach() {
                    it.onSDPOffer(sessionDescription, UUID.fromString(sdpMsg.origin_uuid))
                }
            }

            MessageType.SDP_ANSWER -> {
                val sdpMsg = Json.decodeFromString<SDPMessageData>(message.data)
                val sessionDescription = decodeSdp(sdpMsg.sdp);
                if (sessionDescription.type.canonicalForm() != SessionDescription.Type.ANSWER.canonicalForm()) {
                    println(
                        "Invalid SDP: Expected " +
                                SessionDescription.Type.ANSWER.canonicalForm() +
                                " got " +
                                sessionDescription.type.canonicalForm()
                    )
                    return
                }
                signallingServerObservers.forEach {
                    it.onSDPAnswer(
                        sessionDescription,
                        UUID.fromString(sdpMsg.origin_uuid)
                    )
                }
            }

            MessageType.PEER_DISCONNECT -> {
                val disconnectedUuid = UUID.fromString(message.data)
                peerServerObservers.forEach { it.onPeerLeave(disconnectedUuid) }
            }

            MessageType.PEER_JOIN -> {
                val peerJoinMsg = Json.decodeFromString<PeerInfoMessageData>(message.data)
                val peerInfo = PeerInfo(
                    name = peerJoinMsg.peer_name,
                    publicUuid = UUID.fromString(peerJoinMsg.peer_uuid),
                )
                peerServerObservers.forEach { it.onPeerJoin(peerInfo) }
            }

            MessageType.SDP_OFFER_REQ -> {
                val peerInfoMsg = Json.decodeFromString<PeerInfoMessageData>(message.data)
                val peerInfo = PeerInfo(
                    name = peerInfoMsg.peer_name,
                    publicUuid = UUID.fromString(peerInfoMsg.peer_uuid)
                )
                signallingServerObservers.forEach { it.onSDPOfferReq(peerInfo.publicUuid) }
            }

            MessageType.TEST -> {
                println("Test message")
            }

            else -> {
                println("Unhandled message type: ${message.type}")
            }
        }
    }

    interface Observer {
        fun onOpen()
        fun onClose()
    }

    override fun addObserver(observer: SignallingServer.Observer) {
        this.signallingServerObservers.add(observer)
    }

    override fun removeObserver(observer: SignallingServer.Observer) {
        this.signallingServerObservers.remove(observer)
    }

    override fun sendSDPOffer(sessionDescription: SessionDescription, peer: PeerInfo) {
        assert(sessionDescription.type == SessionDescription.Type.OFFER)
        val sdpMessageData = SDPMessageData(
//            origin_name = user.name,
            origin_uuid = user.publicUuid.toString(),
            recipient_uuid = peer.publicUuid.toString(),
            sdp = encodeSdp(sessionDescription)
        )
        send(MessageType.SDP_OFFER, Json.encodeToString(sdpMessageData))
    }

    override fun sendSDPAnswer(sessionDescription: SessionDescription, peer: PeerInfo) {
        assert(sessionDescription.type == SessionDescription.Type.ANSWER)
        val sdpMessageData = SDPMessageData(
//            origin_name = user.name,
            origin_uuid = user.publicUuid.toString(),
            recipient_uuid = peer.publicUuid.toString(),
            sdp = encodeSdp(sessionDescription)
        )
        send(MessageType.SDP_ANSWER, Json.encodeToString(sdpMessageData))
    }

    override fun sendIceCandidate(iceCandidate: IceCandidate, peer: PeerInfo) {
        val iceCandidateMessageData = IceCandidateMessageData(
            origin_uuid = user.publicUuid.toString(),
            recipient_uuid = peer.publicUuid.toString(),
            ice = encodeIce(iceCandidate)
        )
        send(MessageType.ICE_CANDIDATE, Json.encodeToString(iceCandidateMessageData))
    }

    private class WSObserver(
        val c: CooldropServer
    ) : WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            println("CLOSED")
            c.observer.onClose()
            if (c.reconnectOnClose) c.reconnect()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            println("CLOSING")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            println("Server connection failure: ${t.message}")
            c.observer.onClose()
            if (c.reconnectOnClose) c.reconnect()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val message: CooldropIOMessage = Json.decodeFromString(text)
                c.handleMessage(message);
            } catch (e: IllegalArgumentException) {
                println("Error parsing CooldropIO message: ${e.message}")
            } catch (e: Exception) {
                println("Error while parsing CooldropIO message")
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("Opened websocket")
        }
    }

    override fun addObserver(observer: PeerServer.Observer) {
        peerServerObservers.add(observer)
    }

    override fun removeObserver(observer: PeerServer.Observer) {
        peerServerObservers.remove(observer)
    }
}

