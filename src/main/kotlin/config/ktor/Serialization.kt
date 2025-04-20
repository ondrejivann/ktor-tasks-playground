package config.ktor

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.Route
import kotlinx.serialization.json.Json

fun Route.configureRestSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            },
        )
    }
}

// fun Application.configureContentNegotiation() {
//    install(ContentNegotiation) {
//        json(
//            Json {
//                prettyPrint = true
//                isLenient = true
//                ignoreUnknownKeys = true
//            },
//        )
//    }
// }
