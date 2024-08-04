package net.wh64.player.util

import net.wh64.player.enum.OSType

fun getDirectory(): String {
    val home = System.getProperty("user.home")
    return when(getOS()) {
        OSType.WINDOWS -> "${home.replace("\\", "\\\\")}\\\\Music"
        OSType.LINUX, OSType.DARWIN -> "$home/Music"
    }
}

fun getDataDir(): String {
    val home = System.getProperty("user.home")
    return when(getOS()) {
        OSType.WINDOWS -> "${home.replace("\\", "\\\\")}\\\\.whplayer"
        OSType.LINUX, OSType.DARWIN -> "$home/.config/whplayer"
    }
}
