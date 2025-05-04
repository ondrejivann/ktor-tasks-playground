package config.ktor

import config.Environment
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application

val logger = KotlinLogging.logger {}

@Suppress("unused")
fun Application.module() {
    logger.info { "🚀 Starting in ${Environment.current} mode" }

    val logLevel = environment.config.property("logging.level").getString()
    logger.info { "🚀 LogLevel: $logLevel" }
    // configureContentNegotiation() // https://github.com/ExpediaGroup/graphql-kotlin/issues/2025
    rateLimit()
    configureStatusPages()
    configureKoin()
    configureCORS()
    configureDatabase()
    configureAuth()
    configureGraphQL()
    configureRouting()
    configureOpenAPI()
    configureCallLogging()
}
