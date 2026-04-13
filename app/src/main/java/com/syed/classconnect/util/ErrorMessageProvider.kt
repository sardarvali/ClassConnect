package com.syed.classconnect.util

object ErrorMessageProvider {
    fun getMessage(errorCode: ErrorCode): String = when (errorCode) {
        ErrorCode.NETWORK_ERROR -> "No internet connection. Please check your network."
        ErrorCode.SERVER_ERROR -> "Server error. Please try again later."
        ErrorCode.NOT_FOUND -> "Item not found."
        ErrorCode.UNAUTHORIZED -> "Session expired. Please log in again."
        ErrorCode.VALIDATION_ERROR -> "Invalid input. Please check your entry."
        ErrorCode.UNKNOWN -> "Something went wrong. Please try again."
    }
}

