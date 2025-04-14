package domain.exceptions

import common.exceptions.ErrorCodes

class AuthException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = ErrorCodes.AUTHENTICATION_ERROR,
) : DomainException(message = message, errorCode = errorCode, cause = cause)