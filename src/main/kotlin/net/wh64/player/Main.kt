package net.wh64.player

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*
import net.wh64.player.enum.PlayMode
import net.wh64.player.service.ConfigDelegate
import net.wh64.player.service.StateService
import net.wh64.player.ui.theme.DefaultTheme
import net.wh64.player.util.MusicLoader
import net.wh64.player.util.MusicPlayer
import net.wh64.player.util.getDataDir
import net.wh64.player.util.getDirectory
import org.jetbrains.exposed.sql.Database
import java.awt.Dimension
import java.io.File

data class ServiceComponent(val stateService: StateService)
data class MusicComponent(val loader: MusicLoader, val player: MusicPlayer, val service: ServiceComponent)
data class DefaultStates(
	val lock: MutableState<Boolean>,
	val current: MutableState<String>,
	val volume: MutableState<Float>,
	val isPlaying: MutableState<Boolean>,
	val progress: MutableFloatState,
	val queue: MutableState<MutableList<File>>,
	val playMode: MutableState<PlayMode>
)

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun App(comp: MusicComponent, states: DefaultStates) {
	val expandMenu = remember { mutableStateOf(false) }
	val musics = remember { mutableStateOf(comp.loader.getList()) }
	comp.player.init()

	Surface {
		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text("WH Player") },
					actions = {
						IconButton(onClick = { expandMenu.value = true }) {
							Icon(Icons.Filled.MoreVert, "Pause")
						}

						DropdownMenu(expanded = expandMenu.value, onDismissRequest = {
							expandMenu.value = false
						}) {
							DropdownMenuItem(onClick = {}) {
								Text("Setting")
							}
						}
					}
				)
			},
			bottomBar = {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier.fillMaxWidth()
						.height(60.dp)
						.background(Color.Black)
				) {
					val slider = SliderDefaults.colors(
						thumbColor = Color.LightGray,
						activeTrackColor = Color.LightGray,
						inactiveTrackColor = Color.Gray,
						disabledInactiveTrackColor = Color.DarkGray
					)

					Row(
						modifier = Modifier.padding(horizontal = 10.dp)
					) {
						IconButton(enabled = states.current.value != "" || !states.lock.value, onClick = {
							GlobalScope.launch {
								comp.player.lock()
								comp.player.prev()
							}
						}) {
							Icon(Icons.Filled.SkipPrevious, tint = Color.LightGray, contentDescription = null)
						}

						IconButton(enabled = states.current.value != "" || !states.lock.value, onClick = {
							GlobalScope.launch {
								if (states.isPlaying.value) {
									comp.player.lock()
									comp.player.pause()
									return@launch
								}

								comp.player.lock()
								comp.player.play()
							}
						}) {
							if (!states.isPlaying.value) {
								Icon(Icons.Filled.PlayArrow, tint = Color.LightGray, contentDescription = null)
								return@IconButton
							}

							Icon(Icons.Filled.Pause, tint = Color.LightGray, contentDescription = null)
						}

						IconButton(enabled = states.current.value != "" || !states.lock.value, onClick = {
							GlobalScope.launch {
								comp.player.lock()
								comp.player.next()
							}
						}) {
							Icon(Icons.Filled.SkipNext, tint = Color.LightGray, contentDescription = null)
						}
					}

					Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
						Text(states.current.value, color = Color.LightGray)
						Row(verticalAlignment = Alignment.CenterVertically) {
							TimeDuration(comp.player.getCurrent(), states)
							Spacer(Modifier.width(4.dp))

							Slider(
								enabled = states.current.value != "",
								value = states.progress.value,
								onValueChange = {
									comp.player.setCursor(it)
								},
								valueRange = 0f..comp.player.getLength().toFloat(),
								colors = slider,
								modifier = Modifier.width(310.dp).height(20.dp)
							)

							Spacer(Modifier.width(4.dp))
							TimeDuration(comp.player.getLength(), states)
						}
					}

					Slider(
						value = states.volume.value,
						onValueChange = {
							states.volume.value = it
						},
						onValueChangeFinished = {
							comp.player.setVolume(states.volume.value)
						},
						valueRange = 0f..1f,
						colors = slider,
						modifier = Modifier.width(150.dp).padding(horizontal = 10.dp)
					)
				}
			}
		) { contentPadding ->
			LazyColumn(Modifier.padding(contentPadding)) {
				items(musics.value) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.fillMaxWidth().height(50.dp).clickable(states.current.value != it.name && !states.lock.value) {
							GlobalScope.launch {
								if (states.lock.value) {
									return@launch
								}

								if (states.isPlaying.value) {
									comp.player.stop()
								}

								comp.player.lock()
								comp.player.build(it.absolutePath, it.name)
								comp.player.play()
							}
						}
					) {
						Spacer(Modifier.size(15.dp))
						Text(it.name)
					}
				}
			}
		}
	}

	GlobalScope.launch {
		while (true) {
			delay(1000)
			comp.service.stateService.save()
			comp.loader.reload()
			musics.value = comp.loader.getList()

			check(comp)
			checkCache(comp)
			System.gc()
		}
	}
}

@Composable
fun TimeDuration(duration: Long = 0L, states: DefaultStates) {
	val color = Color.Gray
	val size = 10.sp
	val modifier = Modifier.padding(bottom = 5.dp)

	if (states.current.value == "") {
		Text("--:--", color = color, fontSize = size, modifier = modifier)
		return
	}

	var second = duration / 1000
	val minute = (duration / 1000) / 60
	while (second > 60) {
		second -= 60
	}

	fun time(sec: Long): String {
		if (sec < 10) {
			return "0$sec"
		}

		return sec.toString()
	}

	Text("${minute}:${time(second)}", color = color, fontSize = size, modifier = modifier)
}

private fun check(comp: MusicComponent) {
	comp.loader.getList().forEach {
		val target = "${getDataDir()}/converted/${it.name}.wav"
		if (!File(target).exists()) {
			comp.player.convert(it.absolutePath, target)
		}
	}
}

private fun checkCache(comp: MusicComponent) {
	val converted = File("${getDataDir()}/converted")
	converted.listFiles()?.forEach {
		val default = File("${getDirectory()}/${it.name.dropLast(4)}")
		if (!comp.loader.getListString().contains(default.name)) {
			println("remove cache: ${it.name.dropLast(4)}")
			it.delete()
		}
	}
}

@OptIn(DelicateCoroutinesApi::class)
fun main() = application {
	ConfigDelegate.init()
	val loader = MusicLoader()
	val player = MusicPlayer()
	val data = File(getDataDir(), "data.db")
	if (!data.exists()) {
		if (!data.parentFile.exists()) {
			data.parentFile.mkdirs()
		}

		data.createNewFile()
	}

	val database = Database.connect(
		url = "jdbc:sqlite:${data.absolutePath}",
		driver = "org.sqlite.JDBC"
	)

	val stateService = StateService()
	val dim = Dimension(800, 600)
	val services = ServiceComponent(stateService)
	val comp = MusicComponent(loader, player, services)

	val raw = comp.service.stateService.load()
	val lock = remember { mutableStateOf(false) }
	val current = remember { mutableStateOf("") }
	val volume = remember { mutableStateOf(raw.volume) }
	val isPlaying = remember { mutableStateOf(false) }
	val progress = remember { mutableFloatStateOf(0f) }
	val queue = remember { mutableStateOf(mutableListOf<File>()) }
	val playMode = remember { mutableStateOf(PlayMode.valueOf(raw.mode)) }

	val states = DefaultStates(
		lock = lock,
		current = current,
		volume = volume,
		isPlaying = isPlaying,
		progress = progress,
		queue = queue,
		playMode = playMode
	)

	comp.player.setStates(states)
	comp.service.stateService.setStates(states)

	val converted = File(getDataDir(), "converted")
	if (!converted.exists()) {
		converted.mkdir()
	}

	check(comp)

	Window(
		title = "WH Player",
		onCloseRequest = ::exitApplication,
		onKeyEvent = {
			if (it.key == Key.Spacebar) {
				if (states.current.value == "") {
					return@Window false
				}

				GlobalScope.launch {
					if (states.lock.value) {
						return@launch
					}

					comp.player.lock()
					if (!states.isPlaying.value) {
						comp.player.play()
					} else {
						comp.player.pause()
					}
				}

				return@Window true
			}

			false
		}
	) {
		window.size = dim
		window.minimumSize = dim

		DefaultTheme {
			App(comp, states)
		}
	}
}
