package net.wh64.player.util

import net.wh64.player.service.Config
import java.io.File

class MusicLoader {
	private val dir = File(Config.path)
	private var list = mutableListOf<File>()

	init {
		reload()
	}

	fun reload() {
		val filter = dir.listFiles()?.filter { it.name.endsWith(".mp3") }
		list = mutableListOf()

		if (filter != null) {
			list.addAll(filter)
		}
	}

	fun getList(): List<File> {
		return list.toList().sortedBy { it.name }
	}
}