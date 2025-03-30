package application.exceptions

import common.exceptions.ErrorCodes

class BusinessRuleViolationException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = ErrorCodes.BUSINESS_RULE_VIOLATION,
) : ApplicationException(message = message, errorCode = errorCode, cause = cause)