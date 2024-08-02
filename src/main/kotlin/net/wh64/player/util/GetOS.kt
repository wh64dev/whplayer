package net.wh64.player.util

import net.wh64.player.enum.OperatingSystem

fun getOS(): OperatingSystem {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> OperatingSystem.WINDOWS
        os.contains("mac") -> OperatingSystem.DARWIN
        os.contains("linux") -> OperatingSystem.LINUX
        else -> throw IllegalStateException("Unknown operating system $os")
    }
}
