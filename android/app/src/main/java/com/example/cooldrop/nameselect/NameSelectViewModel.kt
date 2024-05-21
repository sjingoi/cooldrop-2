package com.example.cooldrop.nameselect

import androidx.lifecycle.ViewModel
import com.example.cooldrop.base.EditableUserInputState

class NameSelectViewModel : ViewModel() {

    val editableUserInputState = EditableUserInputState()

    val name: String
        get() = editableUserInputState.text

    val allowSubmit: Boolean
        get() = name.trim() != ""

}