package com.example.core.utils

sealed class WorkResult<out R> {
    data class Success<out T>(val data: T) : WorkResult<T>()
    data class Error(val exception: Exception) : WorkResult<Nothing>()
    object Empty : WorkResult<Nothing>()
}