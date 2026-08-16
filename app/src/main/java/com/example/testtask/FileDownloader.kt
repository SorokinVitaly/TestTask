package com.example.testtask

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


suspend fun downloadMap(
    url: String,
    destination: File,
    onProgress: (Float) -> Unit
) = withContext(Dispatchers.IO) {
    val connection = (URL(url).openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = CONNECT_TIMEOUT
        readTimeout = READ_TIMEOUT
        doInput = true
    }

    try {
        connection.connect()
        if (connection.responseCode !in 200..299) {
            throw IOException("HTTP error: ${connection.responseCode}")
        }
        val fileSize = connection.contentLengthLong
        val tempFile = File(destination.absolutePath + TMP)
        try {
            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var downloaded = 0L
                    while (true) {
                        ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) {
                            break
                        }
                        output.write(buffer, 0, count)
                        downloaded += count
                        if (fileSize > 0) {
                            onProgress(downloaded.toFloat() / fileSize.toFloat())
                        }
                    }
                }
            }
            if (!tempFile.renameTo(destination)) {
                throw IOException("Cannot rename temporary file")
            }
        } catch (e: Throwable) {
            tempFile.delete()
            throw e
        }
    } finally {
        connection.disconnect()
    }
}

private const val BUFFER_SIZE = 32 * 1024
private const val CONNECT_TIMEOUT = 15000
private const val READ_TIMEOUT = 30000
private const val TMP = ".tmp"