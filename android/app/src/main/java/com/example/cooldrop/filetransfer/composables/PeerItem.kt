package com.example.cooldrop.filetransfer.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cooldrop.filetransfer.Peer
import com.example.cooldrop.ui.theme.CooldropTheme
import java.util.UUID

@Composable
fun PeerItem(
    peer: Peer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        modifier = modifier
            .border(1.5.dp, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.shapes.medium)
            .width(256.dp)
            .height(256.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = peer.name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .width(128.dp)
                    .height(128.dp)
            )
            Text(
                text = peer.publicUuid.toString(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Light
            )
            LinearProgressIndicator(
                modifier = Modifier.padding(vertical = 16.dp),
                progress = 0f,
                trackColor = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PeerItemPreview() {
    CooldropTheme {
        PeerItem(
            modifier = Modifier.padding(8.dp),
            peer = Peer(UUID.randomUUID(),"Seb's Desktop"),
            onClick = {}
        )
    }
}