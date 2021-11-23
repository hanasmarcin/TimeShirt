package com.example.timeshirt.service

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.timeshirt.models.BleDeviceModel
import com.example.timeshirt.repository.BleSerialCommunicationRepository
import com.example.timeshirt.repository.BleSerialCommunicationRepository.Companion.STATE_CONNECTED
import com.example.timeshirt.repository.BleSerialCommunicationRepository.Companion.STATE_DISCONNECTED
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BleSerialCommunicationService @Inject constructor() : Service() {

    @Inject
    lateinit var repository: BleSerialCommunicationRepository
    private var device: BleDeviceModel? = null

    inner class LocalBinder(intent: Intent?) : Binder() {
        init {
            init(intent)
        }

        fun getService() = this@BleSerialCommunicationService
    }

    // Bind activity/fragment to service
    override fun onBind(intent: Intent?): IBinder = LocalBinder(intent)

    // Start service independently by using intent
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        init(intent)
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        repository.disconnect()
        return super.onUnbind(intent)
    }

    private fun init(intent: Intent?) {
        intent?.getParcelableExtra<BleDeviceModel>("device")?.let { device = it }
        device?.let {
            repository.init(
                it.address,
                onConnectionStateChange = { state ->
                    when (state) {
                        STATE_CONNECTED -> broadcastUpdate(ACTION_GATT_CONNECTED, it)
                        STATE_DISCONNECTED -> broadcastUpdate(ACTION_GATT_DISCONNECTED, it)
                    }
                }, onServicesDiscovered = {
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, it)
                    val cal = Calendar.getInstance()
                    val millis =
                        ((((cal.get(Calendar.HOUR) * 60 + cal.get(Calendar.MINUTE)) * 60) + cal.get(
                            Calendar.SECOND
                        )) * 1000) + cal.get(Calendar.MILLISECOND)
                    repository.send("time_${millis}_end")
                }, onWriteSuccess = {

                })
        }
    }

    private fun broadcastUpdate(action: String, device: BleDeviceModel) {
        val intent = Intent(action).apply { putExtra("device", device) }
        sendBroadcast(intent)
    }

    companion object {
        const val ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
    }
}