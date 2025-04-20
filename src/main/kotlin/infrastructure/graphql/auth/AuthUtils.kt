package infrastructure.graphql.auth

import domain.exceptions.AuthException
import domain.model.user.User
import graphql.schema.DataFetchingEnvironment

/**
 * Utility functions for working with authentication in GraphQL resolvers.
 * Provides convenient methods to access the authenticated user and check
 * authentication status.
 */
object AuthUtils {
    /**
     * Gets the authenticated user from the GraphQL context.
     * If the user is not authenticated, throws an AuthException.
     *
     * @param env The GraphQL data fetching environment
     * @return The authenticated user
     * @throws AuthException If the user is not authenticated
     */
    fun getAuthenticatedUser(env: DataFetchingEnvironment): User {
        val authContext = env.graphQlContext.get<AuthContext>("authContext")
            ?: throw AuthException("Authentication context is missing")

        return authContext.user ?: throw AuthException("User is not authenticated")
    }

    /**
     * Checks if the user is authenticated.
     *
     * @param env The GraphQL data fetching environment
     * @return true if the user is authenticated, otherwise false
     */
    fun isAuthenticated(env: DataFetchingEnvironment): Boolean {
        val authContext = env.graphQlContext.get<AuthContext>("authContext")
        return authContext?.isAuthenticated ?: false
    }
}
