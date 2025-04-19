package domain.ports.driving

import domain.model.auth.command.RegisterUserCommand
import domain.model.auth.response.AuthResponse
import domain.model.user.User

/**
 * Service layer for user management and authentication.
 *
 * This interface defines operations for registering, authenticating, managing users,
 * and handling authentication tokens including OAuth support.
 */
interface UserService {
    /**
     * Registers a new user.
     *
     * Creates a new user account with the provided information and returns authentication tokens.
     *
     * @param command Object containing registration details
     * @return Authentication response containing tokens and user information
     */
    suspend fun registerUser(command: RegisterUserCommand): AuthResponse

    /**
     * Authenticates a user using email and password.
     *
     * @param email The user's email address
     * @param password The user's password
     * @return Authentication response containing tokens and user information
     * @throws AuthException if credentials are invalid
     */
    suspend fun authenticateUser(email: String, password: String): AuthResponse

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param token The refresh token string
     * @return Authentication response containing new tokens and user information
     * @throws AuthException if the refresh token is invalid or expired
     */
    suspend fun refreshToken(token: String): AuthResponse

    /**
     * Retrieves a user by their ID.
     *
     * @param id The user's unique identifier
     * @return The user object
     * @throws EntityNotFoundException if the user is not found
     */
    suspend fun getUserById(id: Int): User

    /**
     * Retrieves a user by their email address.
     *
     * @param email The user's email address
     * @return The user object, or null if not found
     */
    suspend fun getUserByEmail(email: String): User?

    /**
     * Retrieves user information from Google using an access token.
     *
     * @param accessToken The OAuth access token
     * @return User information retrieved from Google
     * @throws BusinessRuleViolationException if the retrieval fails
     */
    // suspend fun getUserInfoFromGoogle(accessToken: String): OAuthUserInfo

    /**
     * Processes an OAuth authorization code and performs user authentication.
     *
     * This method handles the full OAuth flow by exchanging the authorization code for tokens,
     * retrieving user information, and creating or updating the user in the system.
     *
     * @param provider The OAuth provider type (e.g., GOOGLE)
     * @param authorizationCode The authorization code received from the provider
     * @param redirectUri The redirect URI used in the OAuth flow
     * @return Authentication response containing tokens and user information
     */
    // suspend fun processOAuthCode(provider: AuthProvider, authorizationCode: String, redirectUri: String): AuthResponse

    /**
     * Authenticates a user using an OAuth access token.
     *
     * This method retrieves user information from the OAuth provider,
     * creates or updates the user in the system, and generates authentication tokens.
     *
     * @param provider The OAuth provider type (e.g., GOOGLE)
     * @param accessToken The OAuth access token
     * @return Authentication response containing tokens and user information
     */
    // suspend fun authenticateWithOAuth(provider: AuthProvider, accessToken: String): AuthResponse

    /**
     * Invalidates all refresh tokens for a specific user.
     *
     * Used when the user logs out from all devices or changes their password.
     *
     * @param userId The user's ID
     * @return The number of tokens invalidated
     */
    suspend fun invalidateAllTokens(userId: Int): Int
}