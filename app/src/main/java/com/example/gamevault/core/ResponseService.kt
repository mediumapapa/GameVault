package com.example.gamevault.core

sealed class ResponseService<out T> {
    object Loading : ResponseService<Nothing>()
    data class Success<T>(val data: T) : ResponseService<T>()
    data class Error(val error: String) : ResponseService<Nothing>()
}
