package com.example.testtask

import androidx.compose.runtime.Immutable


@Immutable
data class ScreenState(
    val isMainScreen: Boolean = true,
    val title: String = "",
    val totalMemory: Float = 1f,
    val freeMemory: Float = 1f,
)

enum class DownloadState {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

@Immutable
data class ItemState(
    val index: Int,
    val name: String,
    val downloadState: DownloadState,
    val downloadProgress: Float,
)

