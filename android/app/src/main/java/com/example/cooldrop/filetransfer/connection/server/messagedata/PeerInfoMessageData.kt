package com.example.cooldrop.filetransfer.connection.server.messagedata

import kotlinx.serialization.Serializable

@Serializable
data class PeerInfoMessageData(
    val peer_uuid: String,
    val peer_name: String
)

