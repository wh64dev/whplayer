package net.wh64.player.util

import kotlinx.coroutines.*
import net.wh64.player.DefaultStates
import net.wh64.player.enum.PlayMode
import java.io.ByteArrayOutputStream
import java.io.File
import javax.sound.sampled.*
import kotlin.math.log10

class MusicPlayer : MusicPlay {
	var clip: Clip? = null
	private var volumeController: FloatControl? = null
	private var states: DefaultStates? = null
	private lateinit var mode: PlayMode

	override suspend fun play() {
		states!!.isPlaying.value = true
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
		states!!.isPlaying.value = false

		clip?.stop()
		clip?.microsecondPosition = 0
		states!!.progress.value = 0f
		states!!.current.value = ""

		unlock()
	}

	override suspend fun pause() {
		states!!.isPlaying.value = false
		clip?.stop()
		unlock()
	}

	override suspend fun next() {
		clip?.microsecondPosition = clip?.microsecondLength!!

		unlock()
	}

	override suspend fun prev() {
		// TODO: create queue function and prev
		if (getCurrent() > 1500L) {
			clip?.microsecondPosition = 0
			unlock()

			return
		}

		stop()
	}

	override fun setCursor(cursor: Float) {
		clip?.microsecondPosition = (cursor * 1000).toLong()
	}

	override fun setVolume(volume: Float) {
		val dB = 20f * log10(volume.coerceIn(0.0f, 1.0f))
		volumeController?.value = dB
	}

	fun init() {
		mode = states?.playMode!!.value
	}

	fun lock() {
		if (states!!.lock.value) {
			return
		}

		states!!.lock.value = true
	}

	private suspend fun unlock() {
		if (!states!!.lock.value) {
			return
		}

		delay(220)
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
		try {
			val target = "${getDataDir()}/converted/${name}.wav"
			val byteArrayOutputStream = ByteArrayOutputStream()

			val file = File(target)
			if (!file.exists()) {
				convert(path, target, byteArrayOutputStream)
				delay(1000)
			}

			file.inputStream().use {
				it.copyTo(byteArrayOutputStream)
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
		} catch (ex: Exception) {
			ex.printStackTrace()
		}
	}

	fun convert(path: String, target: String, stream: ByteArrayOutputStream? = null) {
		var process: Process? = null

		try {
			val commands = listOf(
				"ffmpeg",
				"-i", path,
				"-f", "wav",
				"-ar", "44100",
				"-ac", "2",
				"-n",
				target
			)

			val processBuilder = ProcessBuilder(commands)
			processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
			process = processBuilder.start()

			process.waitFor()
			if (stream != null) {
				process?.inputStream.use {
					it?.copyTo(stream)
				}
			}
		} catch (ex: Exception) {
			throw ex
		} finally {
			process?.destroy()
		}
	}
}