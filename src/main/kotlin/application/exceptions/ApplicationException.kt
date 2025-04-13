package application.exceptions

import common.exceptions.AppException
import common.exceptions.ErrorCodes

abstract class ApplicationException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = ErrorCodes.APPLICATION_ERROR,
) : AppException(message, cause, errorCode)