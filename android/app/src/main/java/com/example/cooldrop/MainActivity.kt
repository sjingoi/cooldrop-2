package com.example.cooldrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
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
        WindowCompat.setDecorFitsSystemWindows(window, false)

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
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavHost(
                    navController = navController
                )
            }
        }
//        val currentBackStack by navController.currentBackStackEntryAsState()
//        val currentDestination = currentBackStack?.destination

//        Scaffold(
//            containerColor = Color.Transparent,
//
//            modifier = Modifier.fillMaxSize()
//        ) { innerPadding ->
//            
//        }
    }
}

@Preview
@Composable
fun CooldropAppPreview() {
    CooldropApp()
}