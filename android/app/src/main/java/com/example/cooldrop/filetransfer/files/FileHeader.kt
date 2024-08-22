package com.example.cooldrop.filetransfer.files

import kotlinx.serialization.Serializable

@Serializable
data class FileHeader(
    val filename: String,
    val filetype: String,
    val filesize: Long,
    val chunksize: Int,
)