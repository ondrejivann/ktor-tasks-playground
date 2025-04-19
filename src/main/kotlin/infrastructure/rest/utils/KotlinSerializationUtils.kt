package infrastructure.rest.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

internal object KotlinSerializationUtils {
    // Change from private to internal
    internal val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    /**
     * Deserialize request body to specified type
     */
    suspend inline fun <reified T> ApplicationCall.receiveObject(): T {
        val requestBody = receiveText()
        return json.decodeFromString<T>(requestBody)
    }

    /**
     * Serialize object to response
     */
    suspend inline fun <reified T> ApplicationCall.respondObject(status: HttpStatusCode = HttpStatusCode.OK, obj: T) {
        val jsonString = json.encodeToString(obj)
        respondText(jsonString, ContentType.Application.Json, status)
    }

    /**
     * Serialize object to JSON string
     */
    inline fun <reified T> toJson(obj: T): String {
        return json.encodeToString(obj)
    }

    /**
     * Deserialize JSON string to object
     */
    inline fun <reified T> fromJson(jsonString: String): T {
        return json.decodeFromString(jsonString)
    }
}