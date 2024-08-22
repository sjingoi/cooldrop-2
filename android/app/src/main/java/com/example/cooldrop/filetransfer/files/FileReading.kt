package com.example.cooldrop.filetransfer.files

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.io.InputStream

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

    val fileHeader = FileHeader(
        filename = fileName,
        filetype = "bruh",
        filesize = fileSize,
        chunksize = CHUNK_SIZE,
    )

    contentResolver.openInputStream(uri)?.let { inputStream: InputStream ->
        onHeader(fileHeader)
        var bytesToSend = fileSize;
        while (bytesToSend > 0) {
            val minBytes: Int = if (CHUNK_SIZE < bytesToSend) CHUNK_SIZE else bytesToSend.toInt()
            val byteArray = ByteArray(minBytes)
            inputStream.read(byteArray, 0, minBytes)
            println("Sending ${minBytes}")
            while (!onChunk(byteArray)) {
                delay(500)
            }
            bytesToSend -= minBytes;
        }
        inputStream.close()
    }


    return true
}