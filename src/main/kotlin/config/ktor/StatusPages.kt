package config.ktor

import application.exceptions.BusinessRuleViolationException
import application.exceptions.FileValidationException
import application.exceptions.ResourceNotFoundException
import common.exceptions.ErrorCodes.INTERNAL_ERROR
import domain.exceptions.AuthException
import domain.exceptions.EntityNotFoundException
import domain.exceptions.ValidationException
import infrastructure.exceptions.InfrastructureException
import infrastructure.rest.dto.ErrorResponse
import infrastructure.rest.utils.KotlinSerializationUtils.respondObject
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respondWithLogging(
                status = HttpStatusCode.BadRequest,
                cause = cause,
                errorCode = cause.errorCode,
                message = cause.message ?: "Validation error",
            )
        }

        exception<EntityNotFoundException> { call, cause ->
            call.respondWithLogging(
                status = HttpStatusCode.NotFound,
                cause = cause,
                errorCode = cause.errorCode,
                message = cause.message ?: "Entity not found",
            )
        }

        exception<ResourceNotFoundException> { call, cause ->
            call.respondWithLogging(
                status = HttpStatusCode.NotFound,
                cause = cause,
                errorCode = cause.errorCode,
                message = cause.message ?: "Resource not found",
            )
        }

        exception<BusinessRuleViolationException> { call, cause ->
            call.respondWithLogging(
                status = HttpStatusCode.Conflict,
                cause = cause,
                errorCode = cause.errorCode,
                message = cause.message ?: "Business rule violation",
            )
        }

        exception<FileValidationException> { call, cause ->
            call.respondWithLogging(
                status = HttpStatusCode.BadRequest,
                cause = cause,
                errorCode = cause.errorCode,
                message = cause.message ?: "File validation error",
            )
        }

        exception<InfrastructureException> { call, cause ->
            call.respondWithLogging(
                status = HttpStatusCode.InternalServerError,
                cause = cause,
                errorCode = cause.errorCode,
                message = cause.message ?: "Infrastructure error",
            )
        }

        exception<Throwable> { call, cause ->
            call.respondWithLogging(
                status = HttpStatusCode.InternalServerError,
                message = "An unexpected error occurred",
                cause = cause,
                errorCode = INTERNAL_ERROR,
            )
        }

        exception<AuthException> { call, cause ->
            call.respondWithLogging(
                status = HttpStatusCode.Unauthorized,
                cause = cause,
                errorCode = cause.errorCode,
                message = cause.message ?: "Authentication error",
            )
        }
    }
}

private suspend fun ApplicationCall.respondWithLogging(
    status: HttpStatusCode,
    cause: Throwable,
    errorCode: String,
    message: String? = cause.message,
) {
    val traceId = UUID.randomUUID().toString()

    MDC.put("traceId", traceId)
    val logger = LoggerFactory.getLogger("ExceptionHandler")

    logger.error("Exception occurred: [traceId: $traceId, code: $errorCode] ${cause.message}", cause)

    // Custom serialization because I cant use ContentNegotiation globally.
    // https://github.com/ExpediaGroup/graphql-kotlin/issues/2025
    this.respondObject(
        status,
        ErrorResponse(
            message = message ?: "An error occurred",
            code = errorCode,
            traceId = traceId,
        ),
    )

    MDC.clear()
}
