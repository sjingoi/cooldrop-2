package com.example.cooldrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cooldrop.ui.theme.CooldropTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CooldropTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InfoPanel(name = "Seb's Phone", publicUUID = "ae073984-5ab7-4176-a5de-c0d934de7cca")
                }
            }
        }
    }
}

@Composable
fun RootUI(name: String, publicUuid: String, peers: Collection<Peer>) {
    CooldropTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                InfoPanel(name, publicUuid)
                PeersArea(peers)
            }
        }
    }
}

@Composable
fun InfoPanel(name: String, publicUUID: String) {
    Column {
        Text("Name: $name")
        Text("UUID: $publicUUID")
    }
}
@Composable
fun PeersArea(peers: Collection<Peer>) {
    Column {
        peers.map{ peer: Peer -> PeerSelector(peer) }
    }
}

@Composable
fun PeerSelector(peer: Peer) {
    Column {
        Text(peer.name)
        Text(peer.uuid.toString())
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RootUI(
        name = "Seb's Preview",
        publicUuid = UUID.randomUUID().toString(),
        peers = setOf(
            Peer("Seb's Phone", UUID.randomUUID()),
            Peer("Seb's Computer", UUID.randomUUID()))
        )
}