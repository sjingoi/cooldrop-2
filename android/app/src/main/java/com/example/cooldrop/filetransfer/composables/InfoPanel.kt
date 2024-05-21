package com.example.cooldrop.filetransfer.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Text(text = "Name: ${user.name}")
            Text(text = "UUID: ${user.publicUuid}")
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