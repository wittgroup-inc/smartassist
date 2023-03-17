package com.wittgroup.smartassistlib.models

import kotlinx.coroutines.flow.Flow

sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val message: String) : Result<T>()
    object Loading : Result<Nothing>()
}
