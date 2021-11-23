package com.example.timeshirt.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.timeshirt.theme.TimeShirtTheme

@Composable
// Stateless AppBar composable
fun AppBar(onBack: (() -> Unit)? = null) {
    SmallTopAppBar(
        title = { MyText(text = "TimeShirt") },
        navigationIcon = {
            onBack?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    TimeShirtTheme(true) {
        AppBar {}
    }
}