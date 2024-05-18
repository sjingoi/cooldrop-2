package com.example.cooldrop.base

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun EditableUserInput(
    keyboardActions: KeyboardActions,
    editableUserInputState: EditableUserInputState = EditableUserInputState(),
    modifier: Modifier = Modifier
) {
    TextField(
        value = editableUserInputState.text,
        onValueChange = editableUserInputState.updateText,
        singleLine = true,
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

class EditableUserInputState() {
    var text by mutableStateOf("")
        private set

    val updateText = { newText: String ->
        text = newText
    }
}