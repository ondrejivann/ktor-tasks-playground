package domain.model.auth

import java.time.LocalDateTime

/**
 * Represents a refresh token used for obtaining new access tokens.
 *
 * Refresh tokens are long-lived credentials stored in the database that allow
 * users to obtain new access tokens without re-authentication. Each refresh token
 * is associated with a specific user and has an expiration date.
 *
 * @property id Unique identifier for the refresh token
 * @property userId Identifier of the user to whom this token belongs
 * @property token The actual refresh token string value
 * @property expiresAt Timestamp when this token expires and becomes invalid
 * @property createdAt Timestamp when this token was created
 */
data class RefreshToken(
    val id: Int,
    val userId: Int,
    val token: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
