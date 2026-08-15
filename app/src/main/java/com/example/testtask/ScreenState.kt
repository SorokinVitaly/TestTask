package com.example.testtask

import androidx.compose.runtime.Immutable


@Immutable
data class ScreenState(
    val isMainScreen: Boolean = true,
    val title: String = "",
    val totalMemory: Float = 1f,
    val freeMemory: Float = 1f,
)