package config.ktor

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    embeddedServer(Netty,
        configure = {
            val cliConfig = CommandLineConfig(args)
            takeFrom(cliConfig.engineConfig)
            loadCommonConfiguration(cliConfig.rootConfig.environment.config)
        }).start(wait = true)
}

fun Application.module() {
    logger.info { "Ktor is starting..." }
    configureKoin()
    configureCORS()
    configureDatabases()
    configureAuth()
    configureGraphQL()
    // configureContentNegotiation()
    configureRouting()
    configureStatusPages()
    configureOpenAPI()
    configureCallLogging()
}

private val logger = KotlinLogging.logger {}