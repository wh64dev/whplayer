package net.wh64.player.service

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.wh64.player.DefaultStates
import net.wh64.player.util.getDataDir
import java.io.File
import java.nio.file.Files

@Serializable
data class RawState(
	val volume: Float,
	val mode: String
)

class StateService {
	private var states: DefaultStates? = null
	private val file = File("${getDataDir()}/tmp/", "state.json")

	init {
		if (!file.exists()) {
			if (!file.parentFile.exists()) {
				file.parentFile.mkdir()
			}

			file.createNewFile()
			Files.write(file.toPath(), "{\"volume\":0.5,\"mode\":\"SINGLE\"}"
				.toByteArray(Charsets.UTF_8))

			Thread.sleep(1000)
		}
	}

	fun setStates(states: DefaultStates) {
		this.states = states
	}

	fun load(): RawState {
		return Json.decodeFromString<RawState>(file.readText())
	}

	fun save() {
		val data = RawState(states!!.volume.value, states!!.playMode.value.name)
		val raw = Json.encodeToString(data)

		Files.write(file.toPath(), raw.toByteArray(Charsets.UTF_8))
	}
}
