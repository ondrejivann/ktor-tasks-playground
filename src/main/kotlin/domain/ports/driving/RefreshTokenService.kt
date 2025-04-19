package domain.ports.driving

import domain.model.auth.RefreshToken
import java.time.LocalDateTime

/**
 * Service layer for managing refresh tokens.
 *
 * This interface defines operations for creating, validating, and managing refresh tokens
 * that are used to obtain new access tokens (JWT) without requiring the user
 * to re-authenticate.
 */
interface RefreshTokenService {
    /**
     * Creates a new refresh token in the database.
     *
     * @param userId The ID of the user to whom the token belongs
     * @param token The refresh token string
     * @param expiresAt The date and time when the token expires
     * @return The created refresh token object
     */
    suspend fun createToken(userId: Int, token: String, expiresAt: LocalDateTime): RefreshToken

    /**
     * Validates a refresh token.
     *
     * Checks if the token exists and hasn't expired yet.
     *
     * @param token The refresh token string to validate
     * @return true if the token is valid, false otherwise
     */
    suspend fun validateToken(token: String): Boolean

    /**
     * Finds a refresh token by its value.
     *
     * @param token The refresh token string
     * @return The RefreshToken object if found, null otherwise
     */
    suspend fun findByToken(token: String): RefreshToken?

    /**
     * Invalidates (removes) a specific refresh token.
     *
     * Used when logging out from a specific device or when a token is compromised.
     *
     * @param token The refresh token string to invalidate
     * @return true if the token was successfully invalidated, false otherwise
     */
    suspend fun invalidateToken(token: String): Boolean

    /**
     * Invalidates all refresh tokens belonging to a user.
     *
     * Used when changing password, account compromise, or complete logout from all devices.
     *
     * @param userId The user ID
     * @return The number of tokens invalidated
     */
    suspend fun invalidateAllUserTokens(userId: Int): Int

    /**
     * Cleans up expired tokens from the database.
     *
     * Should be called periodically as part of database maintenance.
     *
     * @return The number of tokens removed
     */
    suspend fun cleanupExpiredTokens(): Int
}