@file:Suppress("ktlint:standard:no-empty-file")

package domain.ports.driven

// /**
// * Port defining operations for retrieving user information from OAuth providers.
// *
// * This port abstracts the interaction with external OAuth providers, allowing
// * the domain layer to remain independent of specific OAuth implementations.
// */

// interface OAuthPort {
//    /**
//     * Retrieves user information from a specific OAuth provider using the provided access token.
//     *
//     * @param provider The OAuth provider type (e.g., GOOGLE, FACEBOOK)
//     * @param accessToken The OAuth access token obtained during the authentication flow
//     * @return User information retrieved from the provider
//     * @throws AuthenticationException if retrieval fails or the token is invalid
//     */
//    suspend fun getUserInfo(provider: AuthProvider, accessToken: String): OAuthUserInfo
//
//    /**
//     * Exchanges an authorization code for access and refresh tokens.
//     *
//     * @param provider The OAuth provider type (e.g., GOOGLE)
//     * @param code The authorization code to exchange
//     * @param redirectUri The redirect URI used in the authorization request
//     * @return OAuth access token information
//     */
//    suspend fun exchangeCodeForToken(
//        provider: AuthProvider,
//        code: String,
//        redirectUri: String
//    ): OAuthAccessToken
// }
