package domain.model.auth

import kotlinx.serialization.Serializable

/**
 * Represents an active user session in the application.
 *
 * This class is used to maintain the authentication state of a user
 * during their interaction with the application. It contains the tokens
 * needed for authentication and token renewal.
 *
 * In Ktor, this class is typically used with the Sessions feature to
 * manage server-side sessions.
 *
 * @property accessToken JWT access token used for authenticating requests
 * @property refreshToken JWT refresh token used for obtaining new access tokens
 */
@Serializable
data class UserSession(val accessToken: String, val refreshToken: String)
