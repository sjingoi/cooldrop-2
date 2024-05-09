package com.example.cooldrop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cooldrop.ui.theme.CooldropTheme

@Composable
fun TitleBar(modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    Column {
        Row(modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surface)) {
            content()
        }
        Divider(color = MaterialTheme.colorScheme.onSurfaceVariant, thickness = 1.5.dp)
    }
}

@Preview
@Composable
fun TitleBarPreview() {
    CooldropTheme {
        TitleBar()
    }
}