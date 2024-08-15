package com.example.cooldrop

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.io.InputStream

@Serializable
data class FileHeader (
    val filename: String,
    val filetype: String,
    val filesize: Long,
    val chunksize: Int,
    val lastchunksize: Long,
    val chunkcount: Long,
    val type: String
)

const val CHUNK_SIZE: Int = 1024 * 64;

val projection = arrayOf(
    OpenableColumns.DISPLAY_NAME,
    OpenableColumns.SIZE,
)

suspend fun readFile (uri: Uri, contentResolver: ContentResolver, onHeader: (header: FileHeader) -> Unit, onChunk: (chunk: ByteArray) -> Boolean): Boolean {
    val cursor = contentResolver.query(uri, projection, null, null, null) ?: error("Cursor null");
    if (!cursor.moveToFirst()) {
        error("Could not read file properties")
    }

    val fileName: String;
    val fileSize: Long;

    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

    fileName = cursor.getString(displayNameIndex)
    fileSize = cursor.getLong(sizeIndex)

    var chunkCount = fileSize / CHUNK_SIZE
    val lastChunkSize = fileSize % CHUNK_SIZE
    if (lastChunkSize > 0) {
        chunkCount ++;
    }
    val fileHeader = FileHeader(
        type = "header",
        filename = fileName,
        filetype = "bruh",
        filesize = fileSize,
        chunksize = CHUNK_SIZE,
        lastchunksize = lastChunkSize,
        chunkcount = chunkCount,
    )

    contentResolver.openInputStream(uri)?.let { inputStream: InputStream ->
        onHeader(fileHeader)
        val byteArray = ByteArray(CHUNK_SIZE)
        for (i in 1..chunkCount) {
            inputStream.read(byteArray, 0, CHUNK_SIZE)
            while (!onChunk(byteArray)) {
                delay(500)
            }
        }
    }


    return true
}