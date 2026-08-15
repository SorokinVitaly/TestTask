package com.example.testtask

import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class MainViewModel(
    private val filesDir: String,
    private val parsed: List<ParsedRegion>
) : ViewModel() {
    private val _state = MutableStateFlow(ScreenState())
    val state = _state.asStateFlow()

    private val dataPath = Environment.getDataDirectory().path

    init {
        calcFreeMemory()
    }

    fun onBackPressed() {

    }

    fun onItemClick(itemState: ItemState) {

    }

    fun onDownloadClick(itemState: ItemState) {

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