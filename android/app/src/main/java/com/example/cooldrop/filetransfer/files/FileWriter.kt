package com.example.cooldrop.filetransfer.files

import java.io.OutputStream

class FileWriter (
    private val outputStreamWriter: OutputStream,
    private val fileHeader: FileHeader
) {
    var progress: Float = 0F;
    var bytesWritten: Long = 0;

    fun addChunk(byteArray: ByteArray) {
        outputStreamWriter.write(byteArray)
        bytesWritten += byteArray.size
        progress = bytesWritten.toFloat() / fileHeader.filesize.toFloat()

        println("Wrote $bytesWritten / ${fileHeader.filesize} bytes.")
    }

    fun close() {
        outputStreamWriter.close()
    }
}