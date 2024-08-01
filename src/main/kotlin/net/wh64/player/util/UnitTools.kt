package net.wh64.player.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun Int.pxToDp(): Dp {
	return (this / LocalDensity.current.density).dp
}

@Composable
internal fun Int.pxToSp(): TextUnit {
	return (this / LocalDensity.current.fontScale).sp
}

@Stable
val Int.ptd: Dp @Composable get() = pxToDp()

@Stable
val Int.pts: TextUnit @Composable get() = pxToSp()
