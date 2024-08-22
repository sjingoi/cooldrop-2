package com.example.cooldrop.filetransfer.connection.server.messagedata

import kotlinx.serialization.Serializable

@Serializable
data class IceCandidateMessageData(
    val origin_uuid: String,
    val recipient_uuid: String,
    val ice: String
)
