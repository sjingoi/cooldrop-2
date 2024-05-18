package com.example.cooldrop.nameselect.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooldrop.nameselect.NameSelectViewModel

@Composable
fun NameSelectScreen(
    modifier: Modifier = Modifier,
    nameSelectViewModel: NameSelectViewModel = viewModel()
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        NameSelectUI(
            editableUserInputState = nameSelectViewModel.editableUserInputState,
            onSubmit = nameSelectViewModel.onSubmit,
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(64.dp))
    }
}