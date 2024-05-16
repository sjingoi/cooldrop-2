package com.example.cooldrop.nameselect.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NameSelectScreen(
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NameSelectUI(
        onSubmit = onSubmit,
        modifier = modifier
    )
}