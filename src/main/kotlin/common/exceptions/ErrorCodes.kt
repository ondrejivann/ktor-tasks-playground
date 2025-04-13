package common.exceptions

object ErrorCodes {
    // Domain error codes
    const val DOMAIN_ERROR = "DOMAIN_ERROR"
    const val ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND"
    const val VALIDATION_ERROR = "VALIDATION_ERROR"

    // Application error codes
    const val APPLICATION_ERROR = "APPLICATION_ERROR"
    const val RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND"
    const val BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION"
    const val FILE_VALIDATION_ERROR = "FILE_VALIDATION_ERROR"

    // Infrastructure error codes
    const val INFRASTRUCTURE_ERROR = "INFRASTRUCTURE_ERROR"
    const val DATABASE_ERROR = "DATABASE_ERROR"
    const val EXTERNAL_SERVICE_ERROR = "EXTERNAL_SERVICE_ERROR"
    const val FILE_STORAGE_ERROR = "FILE_STORAGE_ERROR"

    // General errors
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
}