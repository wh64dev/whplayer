package net.wh64.player.util

import kotlinx.coroutines.*
import net.wh64.player.DefaultStates
import java.io.ByteArrayOutputStream
import javax.sound.sampled.*
import kotlin.math.log10

class MusicPlayer : MusicPlay {
	var clip: Clip? = null
	private var volumeController: FloatControl? = null
	private var states: DefaultStates? = null

	override suspend fun play() {
		clip?.start()
		unlock()

		withContext(Dispatchers.IO) {
			do {
				if (clip?.microsecondPosition!! >= clip?.microsecondLength!!) {
					break
				}

				states!!.progress.value = (clip?.microsecondPosition!! / 1000).toFloat()
			} while (true)

			stop()
		}
	}

	override suspend fun stop() {
		clip?.stop()
		clip?.microsecondPosition = 0
		states!!.current.value = ""

		unlock()
	}

	override suspend fun pause() {
		clip?.stop()
		unlock()
	}

	override suspend fun next() {
		if (clip?.microsecondPosition!! >= clip?.microsecondLength!!) {
			// TODO: create queue function and next
		}

	}

	override suspend fun prev() {
		// TODO: create queue function and prev
		stop()
	}

	override fun setCursor(cursor: Float) {
		clip?.microsecondPosition = (cursor * 1000).toLong()
	}

	override fun setVolume(volume: Float) {
		val dB = 20f * log10(volume.coerceIn(0.0f, 1.0f))
		volumeController?.value = dB
	}

	private suspend fun unlock() {
		if (!states!!.lock.value) {
			return
		}

		delay(120)
		states!!.lock.value = false
	}

	fun setStates(states: DefaultStates) {
		this.states = states
	}

	fun getCurrent(): Long {
		if (clip?.microsecondPosition == null) {
			return 0L
		}

		return (clip?.microsecondPosition!! / 1000)
	}

	fun getLength(): Long {
		if (clip?.microsecondLength == null) {
			return 0L
		}

		return (clip?.microsecondLength!! / 1000)
	}

	override suspend fun build(path: String, name: String) {
		var process: Process? = null
		try {
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
			process = withContext(Dispatchers.IO) {
				processBuilder.start()
			}

			val byteArrayOutputStream = ByteArrayOutputStream()
			process?.inputStream.use { stream ->
				stream?.copyTo(byteArrayOutputStream)
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
			states!!.current.value = name
			withContext(Dispatchers.IO) {
				process.waitFor()
			}
		} catch (ex: Exception) {
			ex.printStackTrace()
		} finally {
			process?.destroy()
		}
	}
}