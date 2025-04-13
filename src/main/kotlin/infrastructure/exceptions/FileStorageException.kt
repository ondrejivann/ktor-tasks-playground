package infrastructure.exceptions

import common.exceptions.ErrorCodes

class FileStorageException(
    message: String,
    cause: Throwable? = null,
    errorCode: String = ErrorCodes.FILE_STORAGE_ERROR,
) : InfrastructureException(message, cause, errorCode)