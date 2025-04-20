package config.ktor

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.engine.CommandLineConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.loadCommonConfiguration
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        configure = {
            val cliConfig = CommandLineConfig(args)
            takeFrom(cliConfig.engineConfig)
            loadCommonConfiguration(cliConfig.rootConfig.environment.config)
        },
    ).start(wait = true)
}

@Suppress("ktlint:standard:no-consecutive-comments")
fun Application.module() {
    logger.info { "Ktor is starting..." }
    /*
    https://github.com/ExpediaGroup/graphql-kotlin/issues/2025
     */
    // configureContentNegotiation()
    configureStatusPages()
    configureKoin()
    configureCORS()
    configureDatabases()
    configureAuth()
    configureGraphQL()
    configureRouting()
    configureOpenAPI()
    configureCallLogging()
}

private val logger = KotlinLogging.logger {}
