package net.wh64.player.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

private val WantedSansFamily = FontFamily(
	Font(resource = "fonts/WantedSans/WantedSans-Black.ttf", weight = FontWeight.Black),
	Font(resource = "fonts/WantedSans/WantedSans-ExtraBold.ttf", weight = FontWeight.ExtraBold),
	Font(resource = "fonts/WantedSans/WantedSans-Bold.ttf", weight = FontWeight.Bold),
	Font(resource = "fonts/WantedSans/WantedSans-Medium.ttf", weight = FontWeight.Medium),
	Font(resource = "fonts/WantedSans/WantedSans-Regular.ttf", weight = FontWeight.Normal)
)

private val PretendardFamily = FontFamily(
	Font(resource = "fonts/Pretendard/PretendardVariable.ttf", weight = FontWeight.W300)
)

val typography = Typography(
	defaultFontFamily = WantedSansFamily,
	h1 = TextStyle(
		fontWeight = FontWeight.W700,
		fontSize = 36.sp
	),
	h2 = TextStyle(
		fontWeight = FontWeight.W600,
		fontSize = 14.sp
	),
	h3 = TextStyle(
		fontWeight = FontWeight.W600,
		fontSize = 12.sp
	)
)

val contentTypography = Typography(defaultFontFamily = PretendardFamily)

@Composable
fun DefaultTheme(content: @Composable () -> Unit) = MaterialTheme(
	typography = typography,
	content = content
)
