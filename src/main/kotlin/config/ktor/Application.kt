package config.ktor

import config.Environment
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application

val logger = KotlinLogging.logger {}

@Suppress("unused")
fun Application.module() {
    // Získej prostředí několika způsoby pro diagnostiku
    val logLevel = environment.config.property("logging.level").getString()

    val ktorEnvFromSystemEnv = System.getenv("KTOR_ENV") ?: "NENÍ NASTAVENO"
    val ktorEnvFromSystemProp = System.getProperty("KTOR_ENV") ?: "NENÍ NASTAVENO"
    val isDevelopmentProp = System.getProperty("io.ktor.development") ?: "NENÍ NASTAVENO"

    // Vypisuj na různé výstupní proudy pro jistotu
    System.out.println("=== DIAGNOSTIKA PROSTŘEDÍ ===")
    System.out.println("KTOR_ENV (env): $ktorEnvFromSystemEnv")
    System.out.println("KTOR_ENV (prop): $ktorEnvFromSystemProp")
    System.out.println("io.ktor.development: $isDevelopmentProp")
    System.out.println("Environment.current: ${Environment.current}")
    System.out.println("LogLevel: $logLevel")
    System.out.println("===========================")

    // Pokus se vypsat i do error logu, který cloud poskytovatelé často zobrazují
    System.err.println("=== DIAGNOSTIKA PROSTŘEDÍ (STDERR) ===")
    System.err.println("KTOR_ENV (env): $ktorEnvFromSystemEnv")
    System.err.println("KTOR_ENV (prop): $ktorEnvFromSystemProp")
    System.err.println("io.ktor.development: $isDevelopmentProp")
    System.err.println("Environment.current: ${Environment.current}")
    System.err.println("LogLevel: $logLevel")
    System.err.println("===========================")

    logger.info { "🚀 Starting in ${Environment.current} mode" }

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
