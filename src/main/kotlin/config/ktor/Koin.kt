package config.ktor

import config.di.AppModule
import config.di.httpClientModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ksp.generated.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    val envModule = module {
        single { environment.config }
    }

    install(Koin) {
        slf4jLogger()
        modules(AppModule().module, httpClientModule, envModule)
    }
}
