package net.wh64.player.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.wh64.player.DefaultStates
import net.wh64.player.MusicComponent
import net.wh64.player.ui.theme.contentTypography
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Home(comp: MusicComponent, states: DefaultStates, modifier: Modifier = Modifier) {
	val scrollState = rememberScrollState()
	MaterialTheme(typography = contentTypography) {
		Column(modifier = modifier.background(Color(0xff191818)).verticalScroll(scrollState)) {
			comp.loader.getList().forEach {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth().height(40.dp)
						.clickable(
						enabled = states.current.value != it.name && !states.lock.value,
					) {
							GlobalScope.launch {
								if (states.current.value == it.name) {
									return@launch
								}

								if (states.current.value != "") {
									comp.player.stop()
								}

								if (states.lock.value) {
									return@launch
								}

								states.lock.value = true
								states.isPlaying.value = true
								comp.player.build(it.absolutePath, it.name)
								comp.player.play()
							}
						}
				) {
					Spacer(Modifier.width(10.dp))
					Text(it.name, color = Color.White, modifier = Modifier.weight(1f, true))
				}
			}
		}
	}
}
