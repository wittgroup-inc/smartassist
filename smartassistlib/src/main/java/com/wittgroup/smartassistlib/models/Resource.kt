package com.wittgroup.smartassistlib.models

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Loading(val isLoading: Boolean) : Resource<Nothing>()
    data class Error(val exception: Exception) : Resource<Nothing>()
}
