package com.example.timeshirt.service

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.example.timeshirt.repository.BleSerialCommunicationRepository
import dagger.hilt.android.AndroidEntryPoint
import java.lang.String
import javax.inject.Inject

@AndroidEntryPoint
class TimeShirtNotifListenerService @Inject constructor() : NotificationListenerService() {

    @Inject
    lateinit var repository: BleSerialCommunicationRepository

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val hexColor = String.format("%06X", 0xFFFFFF and it.notification.color)
            repository.send("notif_${hexColor}_end")
            super.onNotificationPosted(it)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    private fun getIconDrawable(sbn: StatusBarNotification): Drawable? =
        sbn.notification.smallIcon.loadDrawable(applicationContext)
}

