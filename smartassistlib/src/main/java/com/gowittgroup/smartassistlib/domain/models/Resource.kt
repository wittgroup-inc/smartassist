package com.gowittgroup.smartassistlib.domain.models

import com.gowittgroup.smartassistlib.models.ai.AiTools

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data object Loading : Resource<Nothing>()
    data class Error(val exception: Exception) : Resource<Nothing>()
}


sealed class StreamResource<out T> {
    data class Initiated(val model: AiTools) : StreamResource<Nothing>()
    data class StreamStarted<out T>(val data: T) : StreamResource<T>()
    data class StreamInProgress<out T>(val data: T) : StreamResource<T>()
    data class StreamCompleted(val completed: Boolean) : StreamResource<Nothing>()
    data class Error(val exception: Exception) : StreamResource<Nothing>()
}



fun <T> Resource<T>.successOr(fallback: T): T {
    return (this as? Resource.Success<T>)?.data ?: fallback
}

fun <Nothing> StreamResource<Nothing>.initiatedOr(fallback: AiTools): AiTools {
    return (this as? StreamResource.Initiated)?.model ?: fallback
}

fun <T> StreamResource<T>.startedOr(fallback: T): T {
    return (this as? StreamResource.StreamStarted<T>)?.data ?: fallback
}

fun <T> StreamResource<T>.inProgressOr(fallback: T): T {
    return (this as? StreamResource.StreamInProgress<T>)?.data ?: fallback
}
