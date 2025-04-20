package domain.model.auth

/**
 * Contains details extracted from a verified JWT token.
 *
 * This data class holds the essential information from a successfully
 * verified JWT token, providing convenient access to claims and metadata
 * for authentication and authorization purposes.
 *
 * @property userId The unique identifier of the user extracted from the token
 * @property email The email address of the user (maybe null for refresh tokens)
 * @property issuedAt Timestamp (in milliseconds) when the token was issued
 * @property expiresAt Timestamp (in milliseconds) when the token expires
 */
data class TokenDetails(val userId: Int, val email: String? = null, val issuedAt: Long, val expiresAt: Long)
