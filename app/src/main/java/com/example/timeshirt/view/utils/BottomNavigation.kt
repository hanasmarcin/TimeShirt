package com.example.timeshirt.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.timeshirt.theme.TimeShirtTheme

@Composable
fun BottomNavigation() {
    NavigationBar() {
        NavigationBarItem(
            selected = true,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
            label = { MyText("ONE") })
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "") },
            label = { MyText("TWO") })
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "") },
            label = { MyText("THREE") })
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationPreview() {
    TimeShirtTheme(true) {
        BottomNavigation()
    }
}