package com.example.testtask

import android.app.Application
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.set


class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(ScreenState())
    val state = _state.asStateFlow()
    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    private val mapManager = MapManager(getApplication<Application>().filesDir)
    private val dataPath = Environment.getDataDirectory().path
    private val downloadJobs = mutableMapOf<String, Job>()

    init {
        mapManager.removeTmpFiles()
        val maps: Map<String, DownloadState> =
            mapManager.findDownloadedMaps().associateWith { DownloadState.Completed }
        _state.update { it.copy(downloads = it.downloads + maps) }
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
        val name = region.downloadName
        updateDownloadState(name, DownloadState.Downloading(0f))
        downloadJobs[name] = viewModelScope.launch {
            try {
                mapManager.downloadMap(name) { progress ->
                    updateDownloadState(name, DownloadState.Downloading(progress))
                }
                updateDownloadState(name, DownloadState.Completed)
                calcFreeMemory()
            } catch (t: Throwable) {
                /**
                 * In real project you should map data layer exception to presentation layer
                 * exception to display a clear and readable message to user.
                 * In this case for debugging purposes I'm leaving original exception.
                 *
                 * If download error occurs (this behavior is not described in the task),
                 * re-downloading is possible
                 * */
                val message = t.message ?: "Unknown error"
                updateDownloadState(name, DownloadState.Error(message))
                _events.emit(UiEvent.ShowToast(message))
            } finally {
                downloadJobs.remove(name)
            }
        }
    }

    fun onCancelClick(region: Region) {
        downloadJobs[region.name]?.cancel()
        //updateDownloadState(region.name, DownloadState.NotStarted)
    }

    private fun updateDownloadState(name: String, state: DownloadState) {
        _state.update { it.copy(downloads = it.downloads + (name to state)) }
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