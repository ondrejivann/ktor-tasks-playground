package config.ktor

import application.services.FileServiceImpl.FileNotFoundException
import application.services.FileServiceImpl.InvalidFileException
import infrastructure.rest.dto.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<InvalidFileException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(message = cause.message ?: "Invalid file", code = "INVALID_FILE")
            )
        }

        exception<FileNotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(message = cause.message ?: "File not found", code = "FILE_NOT_FOUND")
            )
        }

        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
}