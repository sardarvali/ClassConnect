package com.syed.classconnect.data.remote

open class NetworkException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
class UnauthorizedException(message: String) : NetworkException(message)
class ForbiddenException(message: String) : NetworkException(message)
class NotFoundException(message: String) : NetworkException(message)
class RateLimitException(message: String) : NetworkException(message)
class ServerException(message: String) : NetworkException(message)

