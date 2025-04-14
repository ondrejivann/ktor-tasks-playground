package domain.ports.driven

import domain.model.auth.RefreshToken

/**
 * Port for refresh token operations.
 *
 * This port defines the operations that the domain layer requires
 * from the persistence layer to manage refresh tokens.
 */
interface RefreshTokenRepository {
    /**
     * Saves a new refresh token to the database.
     *
     * @param refreshToken The refresh token to save
     * @return The saved refresh token
     */
    suspend fun save(refreshToken: RefreshToken): RefreshToken

    /**
     * Finds a refresh token by its token value.
     *
     * @param token The token string to search for
     * @return The refresh token if found, null otherwise
     */
    suspend fun findByToken(token: String): RefreshToken?

    /**
     * Invalidates all refresh tokens for a specific user.
     *
     * @param userId The ID of the user whose tokens should be invalidated
     * @return The number of tokens invalidated
     */
    suspend fun invalidateAllForUser(userId: Int): Int

    /**
     * Deletes a specific refresh token.
     *
     * @param id The ID of the token to delete
     * @return true if the token was deleted, false otherwise
     */
    suspend fun delete(id: Int): Boolean

    /**
     * Removes all expired tokens from the database.
     *
     * @return The number of tokens deleted
     */
    suspend fun deleteExpired(): Int
}