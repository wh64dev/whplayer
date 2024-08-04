package net.wh64.player

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.wh64.player.enum.Pages
import net.wh64.player.enum.PlayMode
import net.wh64.player.service.StateService
import net.wh64.player.ui.modal.Controller
import net.wh64.player.ui.modal.Navigation
import net.wh64.player.ui.pages.Favorite
import net.wh64.player.ui.pages.Home
import net.wh64.player.ui.pages.PlayList
import net.wh64.player.ui.pages.Settings
import net.wh64.player.ui.theme.DefaultTheme
import net.wh64.player.ui.theme.contentTypography
import net.wh64.player.util.MusicLoader
import net.wh64.player.util.MusicPlayer
import org.jetbrains.exposed.sql.Database
import java.awt.Dimension
import java.io.File

data class ServiceComponent(val stateService: StateService)
data class MusicComponent(val loader: MusicLoader, val player: MusicPlayer, val service: ServiceComponent)
data class DefaultStates(
	val lock: MutableState<Boolean>,
	val current: MutableState<String>,
	val volume: MutableState<Float>,
	val page: MutableState<Pages>,
	val isPlaying: MutableState<Boolean>,
	val progress: MutableFloatState,
	val viewNP: MutableState<Boolean>,
	val queue: MutableState<MutableList<String>>,
	val playMode: MutableState<PlayMode>
)

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun App(comp: MusicComponent) {
	val raw = comp.service.stateService.load()
	val lock = remember { mutableStateOf(false) }
	val current = remember { mutableStateOf("") }
	val volume = remember { mutableStateOf(raw.volume) }
	val page = remember { mutableStateOf(Pages.HOME) }
	val viewNP = remember { mutableStateOf(true) }
	val isPlaying = remember { mutableStateOf(false) }
	val progress = remember { mutableFloatStateOf(0f) }
	val queue = remember { mutableStateOf(mutableListOf<String>()) }
	val playMode = remember { mutableStateOf(PlayMode.SINGLE) }
	val states = DefaultStates(
		lock = lock,
		current = current,
		volume = volume,
		page = page,
		isPlaying = isPlaying,
		progress = progress,
		viewNP = viewNP,
		queue = queue,
		playMode = playMode
	)
	val search = remember { mutableStateOf("") }

	comp.player.setStates(states)
	comp.service.stateService.setStates(states)

	Surface {
		Scaffold(
			topBar = {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth().height(56.dp).background(Color(0xff212121))
				) {
					Spacer(Modifier.width(130.dp))

					MaterialTheme(typography = contentTypography) {
						OutlinedTextField(
							value = search.value,
							onValueChange = { search.value = it },
							colors = TextFieldDefaults.textFieldColors(textColor = Color.Black, backgroundColor = Color.White),
							shape = RoundedCornerShape(50.dp),
							placeholder = { Text("Search feature not available") },
							modifier = Modifier.size(width = 216.dp, height = 28.dp)
								.padding(horizontal = 10.dp, vertical = 0.dp)
						)
					}
				}
			}
		) { paddingValues ->
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxSize().padding(paddingValues)
			) {
				Navigation(states)
				when (states.page.value) {
					Pages.HOME -> Home(comp, states, modifier = Modifier.weight(1f).fillMaxHeight())
					Pages.PLAY_LIST -> PlayList()
					Pages.FAVORITE -> Favorite()
					Pages.SETTINGS -> Settings()
				}

				if (viewNP.value) {
					Controller(comp, states)
				} else {
					// IGNORE
				}
			}
		}
	}

	GlobalScope.launch {
		while (true) {
			delay(3000)
			comp.service.stateService.save()
			comp.loader.reload()
			System.gc()

			println("refresher task completed")
		}
	}
}

fun main() = application {
	val loader = MusicLoader()
	val player = MusicPlayer()
	val data = File("data.db")
	if (!data.exists()) {
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

	Window(
		title = "WH Player",
		onCloseRequest = ::exitApplication
	) {
		window.size = dim
		window.minimumSize = dim

		DefaultTheme {
			App(comp)
		}
	}
}
