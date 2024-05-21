package com.example.cooldrop

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

typealias CooldropIOCallback = (String) -> Unit

@Serializable
data class CooldropIOMessage(val type: String, val data: String)

class CooldropIOClient (
    private val url: String,
    private val callbacks: Map<String, CooldropIOCallback> = HashMap()
) {

    private val client: OkHttpClient

    private val request: Request

    private val webSocket: WebSocket
    init {
        val webSocketListener: WebSocketListener = object : WebSocketListener() {
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("CLOSED")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("CLOSING")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("FAIL ${t.message}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val message: CooldropIOMessage = Json.decodeFromString(text)
                    val callback = callbacks.get(message.type)
                    if (callback == null) {
                        println("Unhandled message type: ${message.type}")
                        return
                    }
                    callback.invoke(message.data)
                } catch (e: IllegalArgumentException) {
                    println("Error parsing CooldropIO message")
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("OPENEEEDDDDD!!!!")
            }
        }

        this.client = OkHttpClient()

        this.request = Request.Builder().url(url).build()

        this.webSocket = client.newWebSocket(request, webSocketListener)
    }

    fun closeConnection() {
        client.dispatcher.executorService.shutdown()
    }
}

