package net.wh64.player.util

import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import javax.sound.sampled.*
import kotlin.math.log10

class MusicPlayer(
	val path: String = "",
	val name: String = "",
	val states: DefaultStates? = null
) : MusicPlay {
	private var clip: Clip? = null
	private var volumeController: FloatControl? = null

	override suspend fun play() {
		clip?.start()
		unlocker()

		withContext(Dispatchers.IO) {
			do {
				if (clip?.microsecondPosition!! >= clip?.microsecondLength!!) {
					break
				}
			} while (true)

			stop()
		}
	}

	override suspend fun pause() {
		clip?.stop()
		unlocker()
	}

	override suspend fun stop() {
		clip?.stop()
		clip?.microsecondPosition = 0

		clip?.drain()
		clip?.close()

		states!!.play.value = false
		states.current.value = MusicPlayer("", "")
		unlocker()
	}

	override fun setCursor(cursor: Long) {
		val len = clip?.microsecondLength!!
		println(len)
	}

	override fun setVolume(volume: Float) {
		val dB = 20f * log10(volume.coerceIn(0.0f, 1.0f))
		volumeController?.value = dB
	}

	private suspend fun unlocker() {
		if (!states!!.lock.value) {
			return
		}

		delay(120)
		states.lock.value = false
	}

	init {
		try {
			if ((path != "")) {
				val commands = listOf(
					"ffmpeg",
					"-i", path,
					"-f", "wav",
					"-ar", "44100",
					"-ac", "2",
					"pipe:1"
				)

				val processBuilder = ProcessBuilder(commands)
				processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
				val process = processBuilder.start()

				val byteArrayOutputStream = ByteArrayOutputStream()
				process.inputStream.use { stream ->
					stream.copyTo(byteArrayOutputStream)
				}

				val audioBytes = byteArrayOutputStream.toByteArray()
				val audioFormat = AudioFormat(44100f, 16, 2, true, false)
				val audioInputStream = AudioInputStream(
					audioBytes.inputStream(),
					audioFormat,
					(audioBytes.size / audioFormat.frameSize).toLong()
				)

				val clip = AudioSystem.getClip().apply {
					open(audioInputStream)
					volumeController = getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
					val dB = 20f * log10(states!!.volume.value.coerceIn(0.0f, 1.0f))
					volumeController?.value = dB
				}

				this.clip = clip
				process.waitFor()
			}
		} catch (ex: Exception) {
			ex.printStackTrace()
		}
	}
}