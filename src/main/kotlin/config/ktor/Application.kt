package config.ktor

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

@Suppress("unused")
fun Application.module() {
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
