package domain.ports.driven

import domain.model.auth.AuthProvider
import domain.model.user.User

/**
 * Port for user-related operations.
 *
 * This port defines the operations that the domain layer requires
 * from the persistence layer to manage user entities.
 */
interface UserRepository {
    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return The user if found, null otherwise
     */
    suspend fun findByEmail(email: String): User?

    /**
     * Finds a user by their unique identifier.
     *
     * @param id The user ID to search for
     * @return The user if found, null otherwise
     */
    suspend fun findById(id: Int): User?

    /**
     * Finds a user by provider ID and auth provider type.
     *
     * @param providerId The ID assigned by the OAuth provider
     * @param provider The authentication provider type
     * @return The user if found, null otherwise
     */
    suspend fun findByProviderId(providerId: String, provider: AuthProvider): User?

    /**
     * Creates a new user in the system.
     *
     * @param user The user entity to create
     * @return The created user with assigned ID
     */
    suspend fun create(user: User): User

    /**
     * Updates an existing user in the system.
     *
     * @param user The user entity with updated information
     * @return The updated user
     */
    suspend fun update(user: User): User
}
