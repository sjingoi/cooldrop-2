package com.example.cooldrop

interface Subscribable {
    var listeners: Collection<Listener>

    fun addListener(listener: Listener) {
        listeners += listener
    }

    fun removeListener(listener: Listener) {
        listeners -= listener
    }
}