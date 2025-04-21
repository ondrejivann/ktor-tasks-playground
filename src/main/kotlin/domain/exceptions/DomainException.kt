package domain.exceptions

import common.exceptions.AppException
import common.exceptions.ErrorCodes

abstract class DomainException(message: String, cause: Throwable? = null, errorCode: String = ErrorCodes.DOMAIN_ERROR) :
    AppException(message, cause, errorCode)
