package application.exceptions

import common.exceptions.ErrorCodes

class FileValidationException(message: String, cause: Throwable? = null, errorCode: String = ErrorCodes.FILE_VALIDATION_ERROR) :
    ApplicationException(message, errorCode = errorCode, cause = cause)
