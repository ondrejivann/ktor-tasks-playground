package config.di

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val httpClientModule = module {
    single { provideHttpClient() }
}

@Single
fun provideHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        engine {
            config {
                // OkHttp specifická konfigurace
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
                retryOnConnectionFailure(true)
            }
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.DEFAULT
        }
    }
}