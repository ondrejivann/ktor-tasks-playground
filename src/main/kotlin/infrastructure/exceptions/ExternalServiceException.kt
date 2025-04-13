package infrastructure.exceptions

import common.exceptions.ErrorCodes

class ExternalServiceException(
    service: String,
    message: String,
    cause: Throwable? = null,
    errorCode: String = ErrorCodes.EXTERNAL_SERVICE_ERROR,
) : InfrastructureException("Error in external service $service: $message", cause, errorCode)