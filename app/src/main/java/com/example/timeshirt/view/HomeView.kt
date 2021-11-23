package com.example.timeshirt.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.navigation.NavController
import com.example.timeshirt.navigation.CONNECT_DIALOG_DEST
import com.example.timeshirt.navigation.HOME_DEST
import com.example.timeshirt.ui.utils.AppBar
import com.example.timeshirt.ui.utils.MyText
import com.example.timeshirt.view.utils.WatchLayout
import com.example.timeshirt.viewmodel.HomeViewModel

@ExperimentalGraphicsApi
@ExperimentalMaterial3Api
@Composable
fun HomeView(navController: NavController, homeViewModel: HomeViewModel) {
    Scaffold(
        topBar = { AppBar() },
        content = { HomeViewContent(navController, homeViewModel) },
    )
}

@ExperimentalGraphicsApi
@Composable
fun HomeViewContent(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WatchLayout(watchState = homeViewModel.watchState)
        Button(onClick = {
            navController.navigate(CONNECT_DIALOG_DEST) {
                popUpTo(HOME_DEST)
            }
        }) {
            MyText(text = with(homeViewModel) {
                when {
                    connectedDevice == null && !isDeviceConnecting -> "Connect to a device"
                    isDeviceConnecting -> "Connecting..."
                    connectedDevice != null -> "Connected to ${connectedDevice?.name}"
                    else -> ""
                }
            })
        }
    }
}