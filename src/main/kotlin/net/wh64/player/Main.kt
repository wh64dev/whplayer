package net.wh64.player

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import net.wh64.player.ui.MusicController
import net.wh64.player.ui.MusicList
import net.wh64.player.util.DefaultStates
import net.wh64.player.util.MusicLoader
import net.wh64.player.util.MusicPlayer

@Composable
@Preview
fun App(loader: MusicLoader) {
	val current = remember { mutableStateOf(MusicPlayer("", "")) }
	val volume = remember { mutableStateOf(0.5f) }
	val play = remember { mutableStateOf(false) }
	val lock = remember { mutableStateOf(false) }

	val states = DefaultStates(
		lock = lock,
		play = play,
		volume = volume,
		current = current
	)

	Surface(modifier = Modifier.fillMaxSize()) {
		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text("WH Player") },
					navigationIcon = {
						IconButton(onClick = {}) {
							Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
						}
					}
				)
			},
			bottomBar = {
				MusicController(states, modifier = Modifier.fillMaxWidth())
			},
			modifier = Modifier.fillMaxSize()
		) { paddingValues ->
			MusicList(
				loader = loader,
				states = states,
				modifier = Modifier.fillMaxSize().padding(paddingValues = paddingValues)
			)
		}
	}
}

fun main() = application {
	val loader = MusicLoader()
	Window(title = "WH Player", onCloseRequest = ::exitApplication) {
		App(loader)
	}
}
