package net.wh64.player.util

interface MusicPlay {
	suspend fun play()
	suspend fun pause()
	suspend fun stop()
	fun setCursor(cursor: Long)
	fun setVolume(volume: Float)
}
