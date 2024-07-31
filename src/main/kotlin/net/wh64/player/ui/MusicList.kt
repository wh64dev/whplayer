package net.wh64.player.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.wh64.player.util.DefaultStates
import net.wh64.player.util.MusicLoader
import net.wh64.player.util.MusicPlayer
import java.io.File

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun MusicList(
	loader: MusicLoader,
	states: DefaultStates,
	modifier: Modifier = Modifier
) {
	val musics = remember { mutableStateOf(loader.getList()) }

	LazyColumn(modifier = modifier) {
		items(musics.value) {
			Card(
				modifier = Modifier.fillMaxWidth().height(50.dp).clickable(enabled = true) {
					GlobalScope.launch {
						if (states.lock.value) {
							return@launch
						}
						if (states.current.value.path != "") {
							states.current.value.stop()
						}

						states.play.value = true
						states.lock.value = true
						states.current.value = MusicPlayer(
							path = it.absolutePath,
							name = it.name,
							states = states
						)
						states.current.value.play()
					}
				}
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Spacer(Modifier.padding(horizontal = 10.dp))
					Text(it.name)
				}
			}
		}
	}

	GlobalScope.launch {
		do {
			delay(5000)
			reload(loader, musics)
		} while (true)
	}
}

private fun reload(loader: MusicLoader, lists: MutableState<List<File>>) {
	loader.reload()
	lists.value = loader.getList()
}
