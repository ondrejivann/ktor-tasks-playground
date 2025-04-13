package domain.exceptions

import common.exceptions.ErrorCodes

class ValidationException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = ErrorCodes.VALIDATION_ERROR,
) : DomainException(message = message, errorCode = errorCode, cause = cause)