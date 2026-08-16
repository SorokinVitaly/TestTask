package com.example.testtask

import android.app.Application
import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(ScreenState())
    val state = _state.asStateFlow()

    private val dataPath = Environment.getDataDirectory().path
    //    private val filesDir: String,

    init {
        calcFreeMemory()
    }

    fun onBackPressed() {
        _state.update { it.copy(path = it.path.dropLast(1)) }
    }

    fun onItemClick(region: Region) {
        if (region.children.isNotEmpty()) {
            _state.update { it.copy(path = it.path + region) }
        }
    }

    fun onDownloadClick(region: Region) {
        Log.e(null,"onDownloadClick: ${region.downloadName}")
    }

    private fun calcFreeMemory() {
        val stat = StatFs(dataPath)
        val blockSize = stat.blockSizeLong
        val total = stat.blockCountLong * blockSize / BYTES_IN_GB
        val free = stat.availableBlocksLong * blockSize / BYTES_IN_GB
        _state.update {
            it.copy(
                totalMemory = total,
                freeMemory = free
            )
        }
    }

    companion object {
        const val BYTES_IN_GB = 1024f * 1024f * 1024f
    }
}