package com.example.timeshirt.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timeshirt.models.BleDeviceModel
import com.example.timeshirt.repository.BleScannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectDialogViewModel @Inject constructor(
    private val bleScannerRepository: BleScannerRepository
) : ViewModel() {
    var devices by mutableStateOf(listOf<Pair<BleDeviceModel, Boolean>>())
        private set

    fun findDevices() = viewModelScope.launch {
        bleScannerRepository.scan { onDevicesFound(it) }
    }

    fun switchDeviceSelection(device: BleDeviceModel) {
        val updatedDevices =
            devices.map { Pair(it.first, if (it.first == device) !it.second else false) }
        devices = updatedDevices
    }

    fun stopScan() = bleScannerRepository.stopScan()

    private fun onDevicesFound(results: List<BleDeviceModel>) {
        devices = results.map {
            val device = it
            Pair(device, devices.firstOrNull { d -> device == d.first }?.second ?: false)
        }.sortedBy { it.first.name }
            .toList()
    }

    override fun onCleared() {
        stopScan()
        super.onCleared()
    }
}