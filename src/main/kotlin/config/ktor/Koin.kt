package config.ktor

import config.di.AppModule
import config.di.httpClientModule
import io.ktor.server.application.*
import org.koin.ksp.generated.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.dsl.module

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(AppModule().module, httpClientModule)
        modules(module {
            single { environment.config }
        })
    }
}