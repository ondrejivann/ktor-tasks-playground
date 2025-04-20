package infrastructure.graphql.auth

import domain.model.user.User
import io.ktor.server.auth.jwt.JWTPrincipal

/**
 * Class containing authentication information for GraphQL context.
 * This class is used to pass information about the authenticated user
 * to GraphQL resolvers.
 */
data class AuthContext(val user: User? = null, val principal: JWTPrincipal? = null) {
    val isAuthenticated: Boolean
        get() = user != null && principal != null
}
