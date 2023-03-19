package com.gowittgroup.smartassistlib.models

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Loading(val isLoading: Boolean) : Resource<Nothing>()
    data class Error(val exception: Exception) : Resource<Nothing>()
}


sealed class StreamResource<out T> {
    data class StreamStarted<out T>(val data: T) : StreamResource<T>()
    data class StreamInProgress<out T>(val data: T) : StreamResource<T>()
    data class StreamCompleted(val completed: Boolean) : StreamResource<Nothing>()
    data class Error(val exception: Exception) : StreamResource<Nothing>()
}

fun <T> Resource<T>.successOr(fallback: T): T {
    return (this as? Resource.Success<T>)?.data ?: fallback
}

fun <T> StreamResource<T>.startedOr(fallback: T): T {
    return (this as? StreamResource.StreamStarted<T>)?.data ?: fallback
}

fun <T> StreamResource<T>.inProgressOr(fallback: T): T {
    return (this as? StreamResource.StreamInProgress<T>)?.data ?: fallback
}
