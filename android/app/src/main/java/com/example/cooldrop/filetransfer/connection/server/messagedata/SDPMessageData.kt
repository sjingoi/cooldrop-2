package com.example.cooldrop.filetransfer.connection.server.messagedata

import kotlinx.serialization.Serializable

@Serializable
data class SDPMessageData(
    val origin_uuid: String,
    val origin_name: String,
    val recipient_uuid: String,
    val sdp: String
)