package net.wh64.player.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.wh64.player.util.DefaultStates

@OptIn(DelicateCoroutinesApi::class)
@Suppress("DEPRECATION")
@Composable
fun MusicController(states: DefaultStates, modifier: Modifier = Modifier) {
	BottomAppBar(
		backgroundColor = Color.Black,
		contentColor = Color.White,
		elevation = 0.dp,
		modifier = modifier
	) {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxWidth()
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth().weight(1f)
			) {
				Row {
					IconButton(
						enabled = states.current.value.path != "",
						onClick = {}
					) {
						Icon(Icons.Filled.SkipPrevious, contentDescription = null)
					}

					IconButton(
						enabled = states.current.value.path != "",
						onClick = {
							GlobalScope.launch {
								if (states.current.value.path != "") {
									if (!states.play.value) {
										states.current.value.play()
									} else {
										states.current.value.pause()
									}

									states.play.value = !states.play.value
								}
							}
						}
					) {
						if (!states.play.value) {
							Icon(Icons.Filled.PlayArrow, contentDescription = null)
							return@IconButton
						}

						Icon(Icons.Filled.Pause, contentDescription = null)
					}

					IconButton(
						enabled = states.current.value.path != "",
						onClick = {}
					) {
						Icon(Icons.Filled.SkipNext, contentDescription = null)
					}
				}

				Text(states.current.value.name, style = MaterialTheme.typography.subtitle1)

				Row(verticalAlignment = Alignment.CenterVertically) {
					when (states.volume.value) {
						0f             -> Icon(Icons.Filled.VolumeMute, contentDescription = null)
						in 0.5f..1.0f -> Icon(Icons.Filled.VolumeUp, contentDescription = null)
						else           -> Icon(Icons.Filled.VolumeDown, contentDescription = null)
					}

					Spacer(modifier = Modifier.width(3.dp))
					Slider(
						colors = SliderDefaults.colors(
							thumbColor = Color.hsl(12f, 1f, 0.7f),
							activeTrackColor = Color.hsl(12f, 1f, 0.7f),
							inactiveTrackColor = Color.hsl(12f, 0.7f, 0.3f),
						),
						value = states.volume.value,
						modifier = Modifier.width(150.dp),
						onValueChange = {
							states.volume.value = it
							states.current.value.setVolume(states.volume.value)
						}
					)

					Spacer(modifier = Modifier.width(10.dp))
				}
			}
		}
	}
}
