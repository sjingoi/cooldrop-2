package com.example.cooldrop.nameselect

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cooldrop.base.EditableUserInputState

class NameSelectViewModel : ViewModel() {

    val editableUserInputState = EditableUserInputState()

    var name = mutableStateOf("")

    val onSubmit: () -> Unit = {}

}