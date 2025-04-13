package infrastructure.exceptions

import common.exceptions.AppException
import common.exceptions.ErrorCodes

abstract class InfrastructureException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = ErrorCodes.INFRASTRUCTURE_ERROR,
) : AppException(message, cause, errorCode)