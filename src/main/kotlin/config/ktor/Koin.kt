package config.ktor

import config.di.AppModule
import io.ktor.server.application.*
import org.koin.ksp.generated.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(AppModule().module)
    }
}