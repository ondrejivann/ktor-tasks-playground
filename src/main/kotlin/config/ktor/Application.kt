package config.ktor

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.engine.embeddedServer

fun main(args: Array<String>) {
    embeddedServer(Netty,
        configure = {
            val cliConfig = CommandLineConfig(args)
            takeFrom(cliConfig.engineConfig)
            loadCommonConfiguration(cliConfig.rootConfig.environment.config)
        }).start(wait = true)
}

fun Application.module() {
    configureKoin()
    configureCORS()
    configureDatabases()
    configureGraphQL()
    configureRouting()
    configureStatusPages()
    configureOpenAPI()
}