package config.ktor

import io.github.cdimascio.dotenv.dotenv
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    setAWSProperties()
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
    configureGraphQL()
    configureRouting()
    configureStatusPages()
    configureOpenAPI()
    configureCallLogging()
}

private val logger = KotlinLogging.logger {}

private fun setAWSProperties() {
    val dotenv = dotenv()
    System.setProperty("AWS_ACCESS_KEY", dotenv["AWS_ACCESS_KEY"] ?: "")
    System.setProperty("AWS_SECRET_KEY", dotenv["AWS_SECRET_KEY"] ?: "")
}