package net.wh64.player.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

private val PretendardFamily = FontFamily(
	Font(resource = "fonts/Pretendard/PretendardVariable.ttf", weight = FontWeight.W300)
)

private val typography = Typography(
	defaultFontFamily = PretendardFamily
)

@Composable
fun DefaultTheme(content: @Composable () -> Unit) = MaterialTheme(
	typography = typography,
	content = content
)
