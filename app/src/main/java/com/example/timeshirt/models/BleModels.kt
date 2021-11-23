package com.example.timeshirt.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class BleDeviceModel(
    val name: String,
    val address: String
) : Parcelable
