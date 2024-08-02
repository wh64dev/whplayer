package net.wh64.player.service

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.wh64.player.DefaultStates
import java.io.File
import java.nio.file.Files

@Serializable
data class RawState(
	val current: String,
	val volume: Float,
	val view_np: Boolean
)

class StateService {
	private var states: DefaultStates? = null
	private val file = File("tmp/", "state.json")

	init {
		if (!file.exists()) {
			if (!file.parentFile.exists()) {
				file.parentFile.mkdir()
			}

			file.createNewFile()
			Files.write(file.toPath(), "{\"current\":\"\",\"volume\":0.5,\"view_np\": false}"
				.toByteArray(Charsets.UTF_8))
		}
	}

	fun setStates(states: DefaultStates) {
		this.states = states
	}

	fun load(): RawState {
		return Json.decodeFromString<RawState>(file.readText())
	}

	fun save() {
		val data = RawState(states!!.current.value, states!!.volume.value, states!!.viewNP.value)
		val raw = Json.encodeToString(data)

		Files.write(file.toPath(), raw.toByteArray(Charsets.UTF_8))
	}
}
