package com.example.timeshirt.view.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.timeshirt.R
import com.example.timeshirt.viewmodel.HomeViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@ExperimentalGraphicsApi
val MINUTE_COLOR = Color.hsv(360 * 48000/65535f, 1f, 1f)
@ExperimentalGraphicsApi
val HOUR_COLOR = Color.hsv(360 * 61000/65535f, 1f, 1f)

@ExperimentalGraphicsApi
@Composable
fun WatchLayout(watchState: HomeViewModel.WatchState) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Image(painterResource(R.drawable.ic_shirt_wrist), "")
        Watch(watchState.hour, watchState.fullFiveMinutes, watchState.isBlinkShowing)
    }
}

@ExperimentalGraphicsApi
@Composable
fun Watch(hour: Int, fiveMinutesSolid: Int, isLit: Boolean) {
    ConstraintLayout(Modifier.size(140.dp)) {
        val diodRefs = List(12) { createRef() }
        for (i in 0..11) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            i == hour && !(i == fiveMinutesSolid && !isLit) -> HOUR_COLOR
                            i < fiveMinutesSolid -> MINUTE_COLOR
                            i == fiveMinutesSolid && isLit -> MINUTE_COLOR
                            else -> Color.Transparent
                        }
                    )
                    .constrainAs(diodRefs[i]) {
                        linkTo(
                            parent.start,
                            parent.end,
                            startMargin = 0.dp,
                            endMargin = 0.dp,
                            bias = (0.5F + sin(i * (PI / 6)) / 2).toFloat()
                        )
                        linkTo(
                            parent.top,
                            parent.bottom,
                            topMargin = 0.dp,
                            bottomMargin = 0.dp,
                            bias = (0.5F - cos(i * (PI / 6)) / 2).toFloat()
                        )
                    }
            )
        }
    }
}

@ExperimentalGraphicsApi
@Preview
@Composable
fun WatchPreview() {
    WatchLayout(HomeViewModel.WatchState(7, 3, true))
}