package com.example.cooldrop

import kotlinx.coroutines.CoroutineDispatcher
import java.util.UUID

data class Peer(val name: String, val uuid: UUID) : Subscribable {
    override var listeners: Collection<Listener> = setOf()
}