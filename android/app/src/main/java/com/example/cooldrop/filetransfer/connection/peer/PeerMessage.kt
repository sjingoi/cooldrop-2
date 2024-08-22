package com.example.cooldrop.filetransfer.connection.peer

import kotlinx.serialization.Serializable

@Serializable
data class PeerMessage(
    val type: String,
    val data: String,
)
