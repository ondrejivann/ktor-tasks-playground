package domain.ports.driven

import domain.model.auth.TokenDetails
import java.util.UUID

/**
 * Port defining operations for JWT token manipulation.
 *
 * This port is responsible for generating and verifying JWT (JSON Web Tokens)
 * used for authentication and authorization purposes within the application.
 * It provides an abstraction layer over the actual JWT implementation,
 * following the hexagonal architecture principle of keeping domain logic
 * independent of external technical details.
 */
interface JwtPort {
    /**
     * Generates an access token for the specified user.
     *
     * Access tokens are short-lived credentials used to authenticate API requests.
     * They typically contain user identification and permission claims.
     *
     * @param userId The unique identifier of the user
     * @param email The email address of the user, included as a claim in the token
     * @return A signed JWT access token as a string
     */
    fun generateAccessToken(userId: Int, email: String): String

    /**
     * Generates a refresh token for the specified user.
     *
     * Refresh tokens are long-lived credentials used to obtain new access tokens
     * without requiring the user to re-authenticate. They should be handled
     * with higher security measures than access tokens.
     *
     * @param userId The unique identifier of the user
     * @return A signed JWT refresh token as a string
     */
    fun generateRefreshToken(userId: Int): String

    /**
     * Verifies and validates a JWT token.
     *
     * This method checks if the token is valid, properly signed, and not expired.
     * It extracts and returns the claims contained in the token if valid.
     *
     * @param token The JWT token string to verify
     * @return TokenDetails object containing the extracted claims if the token is valid,
     *         null otherwise
     */
    fun verifyToken(token: String): TokenDetails?
}