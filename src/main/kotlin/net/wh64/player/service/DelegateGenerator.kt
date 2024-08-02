package net.wh64.player.service

import kotlin.reflect.KProperty

interface DelegateGenerator<T> {
    operator fun getValue(ref: Any, property: KProperty<*>): T
    operator fun setValue(ref: Any, property: KProperty<*>, value: T) {}
}