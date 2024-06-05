package com.example.cooldrop.filetransfer.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cooldrop.User
import com.example.cooldrop.ui.theme.CooldropTheme
import java.util.UUID

@Composable
fun InfoPanel(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = "Name: ${user.name}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "UUID: ${user.publicUuid}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
fun InfoPanelPreview() {
    CooldropTheme {
        InfoPanel(
            user = User(
                name = "Seb's Device",
                publicUuid = UUID.randomUUID(),
                privateUuid = UUID.randomUUID()
            ),
            onClick = {}
        )
    }
}