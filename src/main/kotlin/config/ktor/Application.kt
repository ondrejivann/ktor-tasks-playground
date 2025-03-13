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

private val logger = KotlinLogging.logger {}

fun Application.module() {
    logger.info { "Ktor is starting..." }
    configureKoin()
    configureCORS()
    configureDatabases()
    configureGraphQL()
    configureRouting()
    configureStatusPages()
    configureOpenAPI()
    configureCallLogging()
}