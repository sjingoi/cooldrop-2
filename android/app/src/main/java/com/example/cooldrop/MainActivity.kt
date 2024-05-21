package com.example.cooldrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.cooldrop.ui.theme.CooldropTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.util.Scanner

@Serializable
data class CooldropSocketMessage(val type: String, val data: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val client = HttpClient(CIO) {
//            install(WebSockets) {
//                pingInterval = 20_000
//            }
//        }
//
//        runBlocking {
//            client.webSocket(method = HttpMethod.Get, host = "192.168.0.60", port = 8080, path = "/") {
//
//                val othersMessage = incoming.receive() as? Frame.Text
//                println(othersMessage?.readText())
//                val sampleMessage = CooldropSocketMessage("test", "HELLO")
//                val myMessage = Json.encodeToString(sampleMessage)
//                send(myMessage)
//            }
//        }

        val io = CooldropIOClient("ws://192.168.0.60:8080")



        enableEdgeToEdge()
        setContent {
            CooldropApp()
        }
    }
}

@Composable
fun CooldropApp() {
    CooldropTheme {
        val navController = rememberNavController()
//        val currentBackStack by navController.currentBackStackEntryAsState()
//        val currentDestination = currentBackStack?.destination

        Scaffold(modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavHost(
                    navController = navController
                )
            }
        }
    }
}

@Preview
@Composable
fun CooldropAppPreview() {
    CooldropApp()
}