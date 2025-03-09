package config.ktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
    }
}