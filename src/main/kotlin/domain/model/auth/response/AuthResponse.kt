package domain.model.auth.response

import domain.model.user.User

/**
 * Contains the result of a successful authentication.
 *
 * This class encapsulates the tokens and user information returned
 * after a successful login, registration, or token refresh operation.
 *
 * @property accessToken JWT access token for API authorization
 * @property refreshToken Token used to get a new access token when it expires
 * @property user The authenticated user object
 * @property expiresIn Time in seconds until the access token expires
 */
data class AuthResponse(val accessToken: String, val refreshToken: String, val user: User, val expiresIn: Long)
