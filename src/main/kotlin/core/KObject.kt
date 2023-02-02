package core

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

open class Object<T> {
    private val properties = hashMapOf<String, KProperty1<Object<T>, T>>()

    fun mapping() {
        this::class.memberProperties.forEach{ properties[it.name] = it as KProperty1<Object<T>, T> }
    }

    operator fun get(name: String): T = properties[name]!!.get(this)
    operator fun get(name: Char): T = this[name.toString()]

    operator fun contains(name: String) = name in properties.keys

    override fun toString()
            = properties.map { "${it.key}: ${it.value.get(this)}" }
        .joinToString(", ")
}
open class MutableObject<T> {
    private val properties = hashMapOf<String, KMutableProperty1<MutableObject<T>, T>>()

    fun mapping() {
        this::class.memberProperties.forEach{ properties[it.name] = it as KMutableProperty1<MutableObject<T>, T> }
    }

    operator fun get(name: String): T = properties[name]!!.get(this)
    operator fun get(name: Char): T = this[name.toString()]

    operator fun set(name: String, value: T) {
        properties[name]?.set(this, value)
    }
    operator fun set(name: Char, value: T) {
        this[name.toString()] = value
    }

    override fun toString()
            = properties.map { "${it.key}: ${it.value.get(this)}" }
        .joinToString(", ")
}