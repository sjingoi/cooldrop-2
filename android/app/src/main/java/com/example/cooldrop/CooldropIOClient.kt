package com.example.cooldrop

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

typealias CooldropIOCallback = (String) -> Unit

@Serializable
data class CooldropIOMessage(val type: String, val data: String)

object MessageType {
    const val TEST = "test"
    const val PRIVATE_UUID = "private-uuid"
    const val PUBLIC_UUID = "public-uuid"
    const val PRIVATE_UUID_REQ = "private-uuid-req"
    const val SDP_OFFER = "sdp-offer"
    const val SDP_ANSWER = "sdp-answer"
    const val SDP_OFFER_REQ = "sdp-offer-req"
    const val ICE_CANDIDATE = "ice-candidate"
    const val PEER_DISCONNECT = "peer-disconnect"
}

object ConnectionStatus {
    const val CONNECTED = "connected"
    const val DISCONNECTED = "disconnected"
}

class CooldropIOClient (
    private val url: String,
    private val onConnectionClose: () -> Unit,
    private val callbacks: MutableMap<String, CooldropIOCallback> = HashMap()
) {

    private var client: OkHttpClient = OkHttpClient()

    private val request: Request = Request.Builder().url(url).build()

    private var reconnectOnClose: Boolean = true

    private val webSocketListener: WebSocketListener = object : WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            println("CLOSED")
            onConnectionClose.invoke()
            if(reconnectOnClose) reconnect()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            println("CLOSING")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            println("Server connection failure: ${t.message}")
            onConnectionClose.invoke()
            if(reconnectOnClose) reconnect()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val message: CooldropIOMessage = Json.decodeFromString(text)
                val callback = callbacks.get(message.type)
                if (callback == null) {
                    println("Unhandled message type: ${message.type}")
                    return
                }
                try {
                    callback.invoke(message.data)
                } catch (e: Exception) {
                    println("Error while invoking callback ${message.type} : $e")
                }
            } catch (e: IllegalArgumentException) {
                println("Error parsing CooldropIO message")
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("Opened websocket")
        }
    }

    private lateinit var webSocket: WebSocket

//    val connectionStatus: String

    private fun initSocket() {
        client = OkHttpClient()
        webSocket = client.newWebSocket(request, webSocketListener)
        client.dispatcher.executorService.shutdown()
    }


    fun connect() {
        if(::webSocket.isInitialized) webSocket.cancel()
        reconnectOnClose = true
        initSocket()
    }

    fun reconnect() {
        initSocket()
    }

    fun disconnect() {
        if(::webSocket.isInitialized) webSocket.close(1000, "Connection closed by client")
        reconnectOnClose = false
    }

    fun addCallback(type: String, callback: CooldropIOCallback) {
        this.callbacks[type] = callback
    }

    fun send(type: String, data: String) {
        val cooldropIOMessage = CooldropIOMessage(type, data)
        val strMessage = Json.encodeToString(cooldropIOMessage)
        this.webSocket.send(strMessage)
    }
}

