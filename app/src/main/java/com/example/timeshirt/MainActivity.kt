package com.example.timeshirt

import android.Manifest
import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.timeshirt.models.BleDeviceModel
import com.example.timeshirt.navigation.CONNECT_DIALOG_DEST
import com.example.timeshirt.navigation.HOME_DEST
import com.example.timeshirt.service.BleSerialCommunicationService
import com.example.timeshirt.service.TimeShirtNotifListenerService
import com.example.timeshirt.theme.TimeShirtTheme
import com.example.timeshirt.view.ConnectDialogView
import com.example.timeshirt.view.HomeView
import com.example.timeshirt.viewmodel.ConnectDialogViewModel
import com.example.timeshirt.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @ObsoleteCoroutinesApi
    @ExperimentalGraphicsApi
    @ExperimentalComposeUiApi
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN
            ), 164
        )
        startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        setContent {
            TimeShirtTheme {
                window.statusBarColor = MaterialTheme.colorScheme.surface.toArgb()
                window.navigationBarColor = MaterialTheme.colorScheme.surface.toArgb()

                val navController = rememberNavController()

                NavHost(navController, startDestination = HOME_DEST) {
                    composable(HOME_DEST) {
                        val homeViewModel = hiltViewModel<HomeViewModel>()
                        InitHome(homeViewModel)
                        HomeView(navController, homeViewModel)
                    }
                    dialog(CONNECT_DIALOG_DEST) {
                        InitConnectDialog()
                        val connectDialogViewModel = hiltViewModel<ConnectDialogViewModel>()
                        val homeViewModel = hiltViewModel<HomeViewModel>(remember { navController.getBackStackEntry(HOME_DEST) })
                        connectDialogViewModel.findDevices()
                        ConnectDialogView(
                            connectDialogViewModel.devices,
                            { connectDialogViewModel.switchDeviceSelection(it) },
                            {
                                initBleSerialCommunicationService(it)
                                navController.popBackStack()
                                homeViewModel.startConnecting()
                            })
                        { navController.popBackStack() }
                    }
                }
            }
        }
    }
    
    @ObsoleteCoroutinesApi
    @ExperimentalMaterial3Api
    @ExperimentalGraphicsApi
    @Composable
    private fun InitHome(homeViewModel: HomeViewModel) {
        val broadcastReceiver = remember {
            object: BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == BleSerialCommunicationService.ACTION_GATT_SERVICES_DISCOVERED) {
                        val device = intent.getParcelableExtra<BleDeviceModel>("device")
                        device?.let { homeViewModel.connectDevice(device) }
                        initNotifService()
                    }
                }
            }
        }
        val filter = remember { IntentFilter(BleSerialCommunicationService.ACTION_GATT_SERVICES_DISCOVERED) }
        remember { registerReceiver(broadcastReceiver, filter) }
        initClock(homeViewModel)
    }

    private fun InitConnectDialog() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) unbindService(connection)
    }

    private var bleSerialCommunicationService: BleSerialCommunicationService? = null
    private var isServiceBound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bleSerialCommunicationService =
                (service as? BleSerialCommunicationService.LocalBinder)?.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    @ObsoleteCoroutinesApi
    private val tickerChannel =
        ticker(250, 1000L - Calendar.getInstance().get(Calendar.MILLISECOND), Dispatchers.Main)
    var blinkCount = 0

    @ObsoleteCoroutinesApi
    fun initClock(viewModel: HomeViewModel) {
        val current = Calendar.getInstance()
        viewModel.watchState = HomeViewModel.WatchState(
            current.get(Calendar.HOUR) % 12,
            current.get(Calendar.MINUTE) / 5,
            false
        )
        lifecycleScope.launch {
            for (event in tickerChannel) {
                viewModel.apply {
                    val cal = Calendar.getInstance()
                    val isBlinkShowing =
                        if (blinkCount == cal.get(Calendar.MINUTE) % 5 && !watchState.isBlinkShowing) {
                            blinkCount = 0
                            false
                        } else {
                            if (!watchState.isBlinkShowing) blinkCount++
                            !watchState.isBlinkShowing
                        }
                    viewModel.watchState = HomeViewModel.WatchState(
                        cal.get(Calendar.HOUR) % 12,
                        cal.get(Calendar.MINUTE) / 5,
                        isBlinkShowing
                    )
                }
            }
        }
    }

    private fun initBleSerialCommunicationService(device: BleDeviceModel) {
        Intent(this, BleSerialCommunicationService::class.java)
            .putExtra("device", device)
            .also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
    }

    private fun initNotifService() {
        Intent(this, TimeShirtNotifListenerService::class.java)
            .also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
    }
}
