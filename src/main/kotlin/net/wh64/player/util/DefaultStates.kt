package net.wh64.player.util

import androidx.compose.runtime.MutableState

data class DefaultStates(
	val lock: MutableState<Boolean>,
	val play: MutableState<Boolean>,
	val volume: MutableState<Float>,
	val current: MutableState<MusicPlayer>
)
