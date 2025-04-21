package domain.exceptions

import common.exceptions.ErrorCodes

class EntityNotFoundException(
    entity: String,
    identifier: Any,
    cause: Throwable? = null,
    errorCode: String = ErrorCodes.ENTITY_NOT_FOUND,
) : DomainException(message = "$entity with identifier $identifier not found", errorCode = errorCode, cause = cause)
