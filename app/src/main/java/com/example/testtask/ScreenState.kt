package com.example.testtask

import androidx.compose.runtime.Immutable


@Immutable
data class ScreenState(
    val path: List<Region> = emptyList(),
    val downloads: Map<String, DownloadState> = emptyMap(),
    val totalMemory: Float = 1f,
    val freeMemory: Float = 1f,
)

@Immutable
data class LocalState(
    val isMainScreen: Boolean,
    val title: String,
    val items: List<Region>
)

sealed interface DownloadState {
    data object NotStarted : DownloadState
    data class Downloading(val progress: Float) : DownloadState
    data object Completed : DownloadState
    data class Error(val message: String) : DownloadState
}

sealed interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
}