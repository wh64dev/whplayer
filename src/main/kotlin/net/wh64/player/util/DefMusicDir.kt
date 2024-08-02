package net.wh64.player.util

import net.wh64.player.enum.OperatingSystem

fun getDirectory(): String {
    val home = System.getProperty("user.home")
    return when(getOS()) {
        OperatingSystem.WINDOWS -> "$home\\Music"
        OperatingSystem.LINUX, OperatingSystem.DARWIN -> "$home/Music"
    }
}
