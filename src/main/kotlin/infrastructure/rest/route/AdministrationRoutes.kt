package infrastructure.rest.route

import config.Environment
import infrastructure.rest.utils.KotlinSerializationUtils.respondObject
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.adminRoutes() {
    route("/administration") {
        get("/diagnostics") {
            val diagnosticInfo = mapOf(
                "environment" to Environment.current.toString(),
                "ktorEnv" to (System.getenv("KTOR_ENV") ?: "not set"),
                "isDevelopment" to (System.getProperty("io.ktor.development") ?: "not set"),
                "logLevel" to (environment.config.property("logging.level").getString()),
                "configFiles" to environment.config.toMap().keys.joinToString(", "),
                "javaVersion" to System.getProperty("java.version"),
            )
            call.respondObject(HttpStatusCode.OK, diagnosticInfo)
        }
    }
}
