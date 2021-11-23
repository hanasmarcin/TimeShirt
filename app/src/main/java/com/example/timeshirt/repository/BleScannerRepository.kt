package com.example.timeshirt.repository

import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.example.timeshirt.models.BleDeviceModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface BleScannerRepository {
    suspend fun scan(onResult: (List<BleDeviceModel>) -> Unit)
    fun stopScan()
}

@Singleton
class BleScannerRepositoryImpl @Inject constructor(
    @ApplicationContext private var applicationContext: Context
) : BleScannerRepository {
    private var scanStarted = false
    private val bluetoothAdapter =
        (applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private val scanner = bluetoothAdapter.bluetoothLeScanner

    override suspend fun scan(onResult: (List<BleDeviceModel>) -> Unit) {
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.firstOrNull()?.device?.fetchUuidsWithSdp()
                onResult(results.map {
                    BleDeviceModel(
                        it.device.name ?: it.device.address,
                        it.device.address
                    )
                })
            }
        }

        val settings: ScanSettings = ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(5000)
            .build()

        if (!scanStarted) scanner.startScan(null, settings, scanCallback)
        scanStarted = true
    }

    override fun stopScan() {
        scanner.stopScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
            }
        })
        scanStarted = false
    }
}