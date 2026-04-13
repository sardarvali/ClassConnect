package com.syed.classconnect.util

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
<<<<<<< HEAD
    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}

=======
    data class Error<T>(
        val message: String,
        val code: Int? = null,
        val errorCode: ErrorCode = ErrorCode.UNKNOWN,
        val exception: Throwable? = null
    ) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}

enum class ErrorCode {
    NETWORK_ERROR,
    SERVER_ERROR,
    NOT_FOUND,
    UNAUTHORIZED,
    VALIDATION_ERROR,
    UNKNOWN
}

>>>>>>> final
