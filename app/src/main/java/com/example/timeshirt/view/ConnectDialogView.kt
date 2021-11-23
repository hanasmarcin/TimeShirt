package com.example.timeshirt.view

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.timeshirt.R
import com.example.timeshirt.models.BleDeviceModel
import com.example.timeshirt.theme.TimeShirtTheme
import com.example.timeshirt.ui.utils.MyText
import timber.log.Timber

@ExperimentalComposeUiApi
@Composable
fun ConnectDialogView(
    devices: List<Pair<BleDeviceModel, Boolean>>,
    onDeviceClick: (BleDeviceModel) -> Unit,
    onDeviceChosen: (BleDeviceModel) -> Unit,
    onDismiss: () -> Unit
) {
    Surface {
        AlertDialog(
            modifier = Modifier.width(300.dp),
            title = {
                    MyText(text = "Connect to a device")
            },
            text = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (devices.isEmpty()) CircularProgressIndicator()
                    else Column {
                        devices.forEach {
                            Box(Modifier.padding(vertical = 2.dp)) {
                                DeviceListItem(it.first, it.second, onDeviceClick)
                            }
                        }
                    }
                }
            },
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = {
                    devices.firstOrNull { it.second }?.first?.let {
                        onDeviceChosen(it)
                    }
                }) {
                    MyText(text = "OK", fontSize=15.sp)
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }
}

@Composable
fun DeviceListItem(
    device: BleDeviceModel,
    isSelected: Boolean,
    onDeviceClick: (BleDeviceModel) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(Modifier.height(56.dp).clip(RoundedCornerShape(50)).toggleable(
        value = isSelected,
        interactionSource = interactionSource,
        indication = rememberRipple(bounded = true),
        onValueChange = { onDeviceClick(device) }
    )
        .run {
            if (isSelected) background(
                color = MaterialTheme.colorScheme.secondaryContainer
            ) else this
        }) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_round_settings_remote_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            MyText(
                text = device.name,
                Modifier.padding(start = 12.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview
@Composable
fun DeviceListItemPreview() {
    TimeShirtTheme {
        DeviceListItem(BleDeviceModel("a", ""), true) { }
    }
}