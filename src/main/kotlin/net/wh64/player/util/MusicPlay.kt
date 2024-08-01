package net.wh64.player.util

interface MusicPlay {
	suspend fun play()
	suspend fun pause()
	suspend fun stop()
	suspend fun prev()
	suspend fun next()
	fun setCursor(cursor: Float)
	fun setVolume(volume: Float)
	suspend fun build(path: String, name: String)
}
