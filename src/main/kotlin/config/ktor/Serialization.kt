package config.ktor

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun Route.configureRestSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
