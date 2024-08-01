package net.wh64.player.ui.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.wh64.player.DefaultStates
import net.wh64.player.MusicComponent
import net.wh64.player.ui.theme.contentTypography

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Controller(comp: MusicComponent, states: DefaultStates) {
	val soundTab = remember { mutableStateOf(false) }
	if (!states.viewNP.value) {
		return
	}

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.width(260.dp).fillMaxHeight().background(Color.Gray)
	) {
		MaterialTheme(typography = contentTypography) {
			Text(states.current.value, fontSize = 12.sp, fontWeight = FontWeight.Bold)

			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Center,
				modifier = Modifier.fillMaxWidth().height(40.dp)
			) {
				val timeModifier = Modifier.fillMaxHeight().width(45.dp)
				val fontSize = 11.sp
				val heap = 6.dp

				fun time(num: Long): String {
					if (num < 10) {
						return "0$num"
					}

					return num.toString()
				}

				Column(
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally,
					modifier = timeModifier
				) {
					if (states.current.value != "") {
						val raw = comp.player.getCurrent()
						var sec = raw / 1000
						val min = (raw / 1000) / 60

						while (sec > 60) {
							sec -= 60
						}

						Text("${min}:${time(sec)}", fontSize = fontSize)
					} else {
						Text("--:--", fontSize = fontSize)
					}

					Spacer(Modifier.height(heap))
				}

				Slider(
					enabled = states.current.value != "",
					value = states.progress.value,
					onValueChange = { comp.player.setCursor(it) },
					interactionSource = MutableInteractionSource(),
					valueRange = 0f..comp.player.getLength().toFloat(),
					modifier = Modifier.width(130.dp)
				)

				Column(
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally,
					modifier = timeModifier
				) {
					if (states.current.value != "") {
						val raw = comp.player.getLength()
						val sec = (raw / 1000).let {
							var ret = it
							while (ret > 60) {
								ret -= 60
							}

							ret
						}
						val min = (raw / 1000) / 60

						Text("$min:${time(sec)}", fontSize = fontSize)
					} else {
						Text("--:--", fontSize = fontSize)
					}

					Spacer(Modifier.height(heap))
				}
			}
		}

		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier.fillMaxWidth()
		) {
			IconButton(onClick = {}) {
				Icon(Icons.Filled.Shuffle, "Pause")
			}

			IconButton(
				enabled = states.current.value != "",
				onClick = {}
			) {
				Icon(Icons.Filled.SkipPrevious, contentDescription = null)
			}

			Button(
				enabled = states.current.value != "",
				onClick = {
					GlobalScope.launch {
						if (states.current.value != "") {
							if (!states.isPlaying.value) {
								states.isPlaying.value = true
								comp.player.play()
							} else {
								states.isPlaying.value = false
								comp.player.pause()
							}
						}
					}
				},
				contentPadding = PaddingValues(0.dp),
				shape = RoundedCornerShape(40.dp),
				modifier = Modifier.size(40.dp)
			) {
				if (!states.isPlaying.value) {
					Icon(Icons.Filled.PlayArrow, contentDescription = null)
					return@Button
				}

				Icon(Icons.Filled.Pause, contentDescription = null)
			}

			IconButton(
				enabled = states.current.value != "",
				onClick = {}
			) {
				Icon(Icons.Filled.SkipNext, contentDescription = null)
			}

			IconButton(onClick = { soundTab.value = !soundTab.value }) {
				Icon(Icons.AutoMirrored.Filled.VolumeUp, null)
			}
		}

		if (soundTab.value) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp, horizontal = 20.dp).background(Color.White)
			) {
				Slider(
					value = states.volume.value,
					onValueChange = { states.volume.value = it },
					onValueChangeFinished = { comp.player.setVolume(states.volume.value) }
				)
			}
		}
	}

	// TODO: write production code here
}
