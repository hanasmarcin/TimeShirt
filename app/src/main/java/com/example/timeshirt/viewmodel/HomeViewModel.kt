package com.example.timeshirt.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.timeshirt.models.BleDeviceModel
import com.example.timeshirt.service.BleSerialCommunicationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.ticker
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {
    var watchState by mutableStateOf(WatchState(0, 0, false))


    data class WatchState(
        var hour: Int,
        var fullFiveMinutes: Int,
        var isBlinkShowing: Boolean
    )

    var isDeviceConnecting by mutableStateOf(false)
        private set

    var connectedDevice: BleDeviceModel? by mutableStateOf(null)
        private set

    fun startConnecting() {
        isDeviceConnecting = true
    }

    fun connectDevice(device: BleDeviceModel) {
        connectedDevice = device
        isDeviceConnecting = false
    }
}