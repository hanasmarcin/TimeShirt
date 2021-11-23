package com.example.timeshirt.repository

import android.bluetooth.*
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.content.Context
import com.example.timeshirt.repository.BleSerialCommunicationRepository.Companion.STATE_CONNECTED
import com.example.timeshirt.repository.BleSerialCommunicationRepository.Companion.STATE_DISCONNECTED
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface BleSerialCommunicationRepository {
    fun init(
        deviceAddress: String,
        onConnectionStateChange: (Int) -> Unit,
        onServicesDiscovered: () -> Unit,
        onWriteSuccess: (String?) -> Unit
    )

    fun connect(deviceAddress: String)
    fun disconnect()
    fun send(message: String)

    companion object {
        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTED = 2
    }
}

@Singleton
class BleSerialCommunicationRepositoryImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) :
    BleSerialCommunicationRepository {

    private var serialPortCharacteristic: BluetoothGattCharacteristic? = null
    private var commandCharacteristic: BluetoothGattCharacteristic? = null
    private var modelNumberCharacteristic: BluetoothGattCharacteristic? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var connectionState = STATE_DISCONNECTED
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothGattCallback: BluetoothGattCallback? = null

    // Initialize service
    override fun init(
        deviceAddress: String,
        onConnectionStateChange: (Int) -> Unit,
        onServicesDiscovered: () -> Unit,
        onWriteSuccess: (String?) -> Unit
    ) {
        val bluetoothManager =
            applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothGattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        connectionState = STATE_CONNECTED
                        onConnectionStateChange(connectionState)
                        bluetoothGatt?.discoverServices()
                        // successfully connected to the GATT Server
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        connectionState = STATE_DISCONNECTED
                        onConnectionStateChange(connectionState)
                        // disconnected from the GATT Server
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                if (status == GATT_SUCCESS) {
                    val gattCharacteristics = gatt?.services?.flatMap { it.characteristics }
                    serialPortCharacteristic =
                        gattCharacteristics?.firstOrNull { it.uuid.toString() == SERIAL_PORT_UUID }
                    commandCharacteristic =
                        gattCharacteristics?.firstOrNull { it.uuid.toString() == COMMAND_UUID }
                    modelNumberCharacteristic =
                        gattCharacteristics?.firstOrNull { it.uuid.toString() == MODEL_NUMBER_STRING_UUID }
                    onServicesDiscovered()
                } else Timber.d("Service discovery returned status $status")
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                synchronized(this) {
                    when (status) {
                        GATT_SUCCESS -> {
                            if (messagesList.isNotEmpty()) {
                                serialPortCharacteristic?.value =
                                    messagesList.removeAt(0).toByteArray(charset("ISO-8859-1"))
                                bluetoothGatt?.writeCharacteristic(serialPortCharacteristic)
                            } else {
                                onWriteSuccess(characteristic?.value?.decodeToString())
                            }
                        }
                        WRITE_NEW_CHARACTERISTIC -> {

                        }
                        else -> Timber.e("Communication error with status $status")
                    }
                }
            }
        }
        connect(deviceAddress)
    }

    override fun connect(deviceAddress: String) {
        bluetoothAdapter?.let { adapter ->
            bluetoothGattCallback?.let {
                try {
                    val bluetoothDevice = adapter.getRemoteDevice(deviceAddress)
                    bluetoothGatt = bluetoothDevice?.connectGatt(applicationContext, true, it)
                } catch (e: IllegalArgumentException) {
                    Timber.e(e, "Not able to connect")
                }
            } ?: Timber.e("Bluetooth adapter not initialized")
        } ?: Timber.e("Bluetooth adapter not initialized")
    }

    override fun disconnect() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    private val messagesList = mutableListOf<String>()

    override fun send(message: String) {
        val initSending = messagesList.isEmpty()
        messagesList.addAll(message.chunked(8))
        if (initSending) {
            serialPortCharacteristic?.value =
                messagesList.removeAt(0).toByteArray(charset("ISO-8859-1"))
            bluetoothGatt?.writeCharacteristic(serialPortCharacteristic)
        }
    }

    companion object {
        const val SERIAL_PORT_UUID = "0000dfb1-0000-1000-8000-00805f9b34fb"
        const val COMMAND_UUID = "0000dfb2-0000-1000-8000-00805f9b34fb"
        const val MODEL_NUMBER_STRING_UUID = "00002a24-0000-1000-8000-00805f9b34fb"
        private const val WRITE_NEW_CHARACTERISTIC = -1
    }
}