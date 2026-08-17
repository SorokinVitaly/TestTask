package com.example.testtask

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MapManager(private val filesDir: File) {
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)
    private val mapDir: File
        get() = File(filesDir, DIR_MAPS).apply {
            mkdirs()
        }

    suspend fun downloadMap(downloadName: String, onProgress: (Float) -> Unit) =
        withContext(dispatcher) {
            val fileName = downloadName + NAME_SUFFIX
            val destination = File(mapDir, fileName)
            val tempFile = File(mapDir, fileName + TMP)
            val url = URL_PREFIX + fileName

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
                } catch (t: Throwable) {
                    tempFile.delete()
                    throw t
                }
            } finally {
                connection.disconnect()
            }
        }

    fun removeTmpFiles() =
        try {
            mapDir.listFiles()?.forEach {
                if (!it.isDirectory && it.name.endsWith(TMP)) {
                    it.delete()
                }
            }
        } catch (_: Throwable) {}

    fun findDownloadedMaps(): List<String> {
        val list = mutableListOf<String>()
        mapDir.listFiles()?.forEach {
            list.add(it.name.removeSuffix(NAME_SUFFIX))
        }
        return list
    }

    companion object {
        private const val BUFFER_SIZE = 32 * 1024
        private const val CONNECT_TIMEOUT = 15000
        private const val READ_TIMEOUT = 30000
        private const val TMP = ".tmp"
        private const val DIR_MAPS = "maps"
        private const val URL_PREFIX = "https://download.osmand.net/download?standard=yes&file="
        private const val NAME_SUFFIX = "_europe_2.obf.zip"
    }
}