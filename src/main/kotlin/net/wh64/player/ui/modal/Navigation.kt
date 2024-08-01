package net.wh64.player.ui.modal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.wh64.player.DefaultStates
import net.wh64.player.enum.Pages

@Composable
fun Navigation(states: DefaultStates) {
	Column(modifier = Modifier.fillMaxHeight().width(130.dp).background(Color(0xff212121))) {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxWidth().height(169.dp).background(
				Brush.verticalGradient(
					colorStops = arrayOf(0.0f to Color(0x44fd7892), 0.3f to Color(0x00fd7892))
				)
			)
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier.size(width = 82.dp, height = 101.dp)
			) {
				Text("WH Player", fontWeight = FontWeight.W300, fontStyle = FontStyle.Normal, fontSize = 16.sp, color = Color.White)
			}
		}

		Column(modifier = Modifier.fillMaxSize()) {
			NavItem("Home", states, Pages.HOME, states.page.value == Pages.HOME)
			NavItem("Playlist", states, Pages.PLAY_LIST, states.page.value == Pages.PLAY_LIST)
			NavItem("Favorite", states, Pages.FAVORITE,states.page.value == Pages.FAVORITE)
			NavItem("Settings", states, Pages.SETTINGS, states.page.value == Pages.SETTINGS)
		}
	}
}

@Composable
private fun NavItem(name: String, states: DefaultStates, page: Pages, selected: Boolean = false) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.fillMaxWidth().height(30.dp).clickable {
			states.page.value = page
		}
	) {
		if (selected) {
			Canvas(
				modifier = Modifier.size(width = 4.dp, height = 30.dp),
				onDraw = {
					drawRect(Color(0xfffd7892))
				}
			)
			Spacer(modifier = Modifier.size(10.dp))
			Text(name, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.White)
		} else {
			Spacer(modifier = Modifier.size(14.dp))
			Text(name, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.Gray)
		}
	}
}
