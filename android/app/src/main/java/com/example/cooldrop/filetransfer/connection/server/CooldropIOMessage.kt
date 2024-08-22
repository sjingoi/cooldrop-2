package com.example.cooldrop.filetransfer.connection.server

import kotlinx.serialization.Serializable

@Serializable
data class CooldropIOMessage(
    val type: String,
    val data: String
)
