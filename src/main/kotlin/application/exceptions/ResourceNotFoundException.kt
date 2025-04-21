package application.exceptions

import common.exceptions.ErrorCodes

class ResourceNotFoundException(
    resource: String,
    identifier: Any,
    cause: Throwable? = null,
    errorCode: String = ErrorCodes.RESOURCE_NOT_FOUND,
) : ApplicationException("$resource with identifier $identifier not found", errorCode = errorCode, cause = cause)
