package domain.model.user

import domain.model.auth.AuthProvider
import java.time.LocalDateTime
import java.util.*

/**
 * Represents a user in the system.
 *
 * This entity contains all relevant information about a registered user,
 * including authentication details and personal information. It supports
 * both local authentication and third-party OAuth providers.
 *
 * @property id Unique identifier for the user
 * @property email Email address of the user, serves as the primary contact and identifier
 * @property passwordHash Hashed password for users authenticated locally, null for OAuth users
 * @property firstName User's first name (optional)
 * @property lastName User's last name (optional)
 * @property authProvider Authentication provider type used by this user
 * @property providerId External identifier from the OAuth provider (if applicable)
 * @property createdAt Timestamp when the user account was created
 * @property updatedAt Timestamp when the user account was last updated
 */
data class User(
    val id: Int,
    val email: String,
    val passwordHash: String?,  // Null pro OAuth uživatele
    val firstName: String?,
    val lastName: String?,
    val authProvider: AuthProvider,
    val providerId: String?,  // ID uživatele od poskytovatele OAuth
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
