package net.wh64.player.util

import net.wh64.player.enum.OSType

fun getOS(): OSType {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> OSType.WINDOWS
        os.contains("mac") -> OSType.DARWIN
        os.contains("linux") -> OSType.LINUX
        else -> throw IllegalStateException("Unknown operating system $os")
    }
}
