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

typealias CooldropIOCallback = (String) -> Unit

@Serializable
data class CooldropIOMessage(val type: String, val data: String)

class CooldropIOClient(
    private val url: String,
    private val port: Int,
    private val callbacks: Map<String, CooldropIOCallback> = HashMap()
) {

    fun connect() {
        val client = HttpClient {
            install(WebSockets)
        }
        runBlocking {
            client.webSocket(method = HttpMethod.Get, host = url, port = port, path = "/") {
                val listenerRoutine = launch { listen() }
                listenerRoutine.join()
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.listen() {
        for (message in incoming) {
            try {
                message as? Frame.Text ?: continue
                val ioMessage: CooldropIOMessage = Json.decodeFromString(message.readText())
                println("Receieved message")
                val callback = callbacks.get(ioMessage.type)
                if (callback == null) {
                    println("Unhandled type: " + ioMessage.type)
                    continue
                }
                callback(ioMessage.data)
            } catch (e: IllegalArgumentException) {
                println("Recieved message that was not encoded in CooldropIO")
            } catch (e: Exception) {
                println("Unknown error")
            }
        }
    }

}

