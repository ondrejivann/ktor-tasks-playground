package infrastructure.graphql.exceptions

import application.exceptions.BusinessRuleViolationException
import application.exceptions.FileValidationException
import application.exceptions.ResourceNotFoundException
import common.exceptions.ErrorCodes
import domain.exceptions.AuthException
import domain.exceptions.EntityNotFoundException
import domain.exceptions.ValidationException
import graphql.ErrorType
import graphql.GraphqlErrorBuilder
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import infrastructure.exceptions.InfrastructureException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*
import java.util.concurrent.CompletableFuture

class GraphQLExceptionHandler : DataFetcherExceptionHandler {
    private val logger = KotlinLogging.logger {}
    override fun handleException(
        handlerParameters: DataFetcherExceptionHandlerParameters?,
    ): CompletableFuture<DataFetcherExceptionHandlerResult> {
        requireNotNull(handlerParameters) { "Handler parameters cannot be null" }

        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path
        val traceId = UUID.randomUUID().toString()

        logger.error(exception) { "GraphQL error occurred: [traceId: $traceId] ${exception.message}" }

        val error = when (exception) {
            is ValidationException -> GraphqlErrorBuilder.newError()
                .message("Validation error: ${exception.message}")
                .location(sourceLocation)
                .path(path)
                .errorType(ErrorType.ValidationError)
                .extensions(
                    mapOf(
                        "code" to exception.errorCode,
                        "type" to "VALIDATION_ERROR",
                        "traceId" to traceId,
                    ),
                )
                .build()

            is EntityNotFoundException -> GraphqlErrorBuilder.newError()
                .message("Entity not found: ${exception.message}")
                .location(sourceLocation)
                .path(path)
                .errorType(ErrorType.DataFetchingException)
                .extensions(
                    mapOf(
                        "code" to exception.errorCode,
                        "type" to "NOT_FOUND",
                        "traceId" to traceId,
                    ),
                )
                .build()

            is ResourceNotFoundException -> GraphqlErrorBuilder.newError()
                .message("Resource not found: ${exception.message}")
                .location(sourceLocation)
                .path(path)
                .errorType(ErrorType.DataFetchingException)
                .extensions(
                    mapOf(
                        "code" to exception.errorCode,
                        "type" to "RESOURCE_NOT_FOUND",
                        "traceId" to traceId,
                    ),
                )
                .build()

            is AuthException -> GraphqlErrorBuilder.newError()
                .message("Authentication error: ${exception.message}")
                .location(sourceLocation)
                .path(path)
                .errorType(ErrorType.DataFetchingException)
                .extensions(
                    mapOf(
                        "code" to exception.errorCode,
                        "type" to "AUTHENTICATION_ERROR",
                        "traceId" to traceId,
                    ),
                )
                .build()

            is BusinessRuleViolationException -> GraphqlErrorBuilder.newError()
                .message("Business rule violation: ${exception.message}")
                .location(sourceLocation)
                .path(path)
                .errorType(ErrorType.ValidationError)
                .extensions(
                    mapOf(
                        "code" to exception.errorCode,
                        "type" to "BUSINESS_RULE_VIOLATION",
                        "traceId" to traceId,
                    ),
                )
                .build()

            is FileValidationException -> GraphqlErrorBuilder.newError()
                .message("File validation error: ${exception.message}")
                .location(sourceLocation)
                .path(path)
                .errorType(ErrorType.ValidationError)
                .extensions(
                    mapOf(
                        "code" to exception.errorCode,
                        "type" to "FILE_VALIDATION_ERROR",
                        "traceId" to traceId,
                    ),
                )
                .build()

            is InfrastructureException -> GraphqlErrorBuilder.newError()
                .message("Infrastructure error: ${exception.message}")
                .location(sourceLocation)
                .path(path)
                .errorType(ErrorType.DataFetchingException)
                .extensions(
                    mapOf(
                        "code" to exception.errorCode,
                        "type" to "INFRASTRUCTURE_ERROR",
                        "traceId" to traceId,
                    ),
                )
                .build()

            else -> GraphqlErrorBuilder.newError()
                .message("An unexpected error occurred")
                .location(sourceLocation)
                .path(path)
                .errorType(ErrorType.DataFetchingException)
                .extensions(
                    mapOf(
                        "code" to ErrorCodes.INTERNAL_ERROR,
                        "type" to "INTERNAL_ERROR",
                        "traceId" to traceId,
                    ),
                )
                .build()
        }

        return CompletableFuture.completedFuture(
            DataFetcherExceptionHandlerResult.newResult()
                .error(error)
                .build(),
        )
    }
}
