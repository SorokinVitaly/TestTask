package com.example.testtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class MainViewModelFactory(
    private val filesDir: String,
    private val parsed: List<ParsedRegion>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(filesDir, parsed) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}