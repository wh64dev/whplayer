package net.wh64.player.service

import net.wh64.player.util.getDirectory
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.util.*
import kotlin.reflect.KProperty

object Config {
    private fun useConfig(): ConfigDelegate<String> {
        return ConfigDelegate()
    }

    var path: String by useConfig()
}

@Suppress("UNCHECKED_CAST")
class ConfigDelegate<T>: DelegateGenerator<T> {
    private val file = File("tmp/", "config.properties")
    private val prop = Properties()
    init {
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }

            file.createNewFile()
            Files.write(file.toPath(), """
                path=${getDirectory()}
            """.trimIndent().toByteArray(Charsets.UTF_8))
        } else {
            prop.load(FileInputStream(file))
        }
    }

    override operator fun getValue(ref: Any, property: KProperty<*>): T {
        return prop[property.name] as T
    }

    companion object {
        fun init() {
            // TODO: Production code will not used this code
            val delegate = ConfigDelegate<String>()
            if (!delegate.file.exists()) {
                Thread.sleep(1000)
            }
            return
        }
    }
}
