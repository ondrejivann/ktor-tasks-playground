package common.exceptions

abstract class AppException(message: String, cause: Throwable? = null, val errorCode: String) : RuntimeException(message, cause)
