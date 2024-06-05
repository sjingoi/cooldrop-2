package com.example.cooldrop.nameselect.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cooldrop.base.EditableUserInput
import com.example.cooldrop.base.EditableUserInputState
import com.example.cooldrop.ui.theme.CooldropTheme

@Composable
fun NameSelectUI(
    onSubmit: () -> Unit,
    allowSubmit: Boolean = true,
    modifier: Modifier = Modifier,
    editableUserInputState: EditableUserInputState = EditableUserInputState()
) {
//    val editableUserInputState = EditableUserInputState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .wrapContentWidth()
            .padding(16.dp),
    ) {
        val focusManager = LocalFocusManager.current
        Text(
            text = "Welcome to Cooldrop",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        EditableUserInput(
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                if (allowSubmit) onSubmit()
            }),
            editableUserInputState = editableUserInputState
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                focusManager.clearFocus()
                onSubmit()
            },
            enabled = allowSubmit,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Submit")
        }
    }
}

@Preview
@Composable
fun NameSelectUIPreview() {
    CooldropTheme {
        Surface(onClick = {}) {
            NameSelectUI(onSubmit = {})
        }
    }
}