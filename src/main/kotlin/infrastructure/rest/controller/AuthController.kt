package infrastructure.rest.controller

import io.ktor.server.application.ApplicationCall

/**
 * Controller responsible for authentication operations.
 */
interface AuthController {
    /**
     * Handles user registration request.
     */
    suspend fun register(call: ApplicationCall)

    /**
     * Handles user login request.
     */
    suspend fun login(call: ApplicationCall)

    /**
     * Handles token refresh request.
     */
    suspend fun refreshToken(call: ApplicationCall)

    /**
     * Gets current user profile information.
     */
    suspend fun getCurrentUser(call: ApplicationCall)

    /**
     * Handles user logout request.
     */
    suspend fun logout(call: ApplicationCall)
}
