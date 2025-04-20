package infrastructure.exceptions

import common.exceptions.ErrorCodes

class DatabaseException(message: String, cause: Throwable? = null, errorCode: String = ErrorCodes.DATABASE_ERROR) :
    InfrastructureException(message, cause, errorCode)
