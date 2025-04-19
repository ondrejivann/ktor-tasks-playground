package infrastructure.graphql.auth

import com.expediagroup.graphql.server.ktor.DefaultKtorGraphQLContextFactory
import domain.exceptions.EntityNotFoundException
import domain.ports.driving.UserService
import graphql.GraphQLContext
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import org.koin.core.annotation.Single

/**
 * Factory for creating GraphQL context with authentication information.
 * This class extends the default Ktor GraphQL context factory to include
 * authentication details for the current request.
 */
@Single
class GraphQLContextFactory(
    private val userService: UserService
) : DefaultKtorGraphQLContextFactory() {
    /**
     * Generates a GraphQL context for the current request.
     * Extracts JWT authentication information and creates an AuthContext
     * that can be accessed by resolvers.
     *
     * @param request The current HTTP request
     * @return GraphQL context with authentication information
     */
    override suspend fun generateContext(request: ApplicationRequest): GraphQLContext {
        // Získáme základní kontext od rodičovské třídy
        val context = super.generateContext(request)

        // Získáme JWT principal, pokud existuje
        val principal = request.call.principal<JWTPrincipal>()

        // Připravíme AuthContext
        var authContext: AuthContext? = null

        if (principal != null) {
            val userId = principal.payload.getClaim("sub").asString().toIntOrNull()
            if (userId != null) {
                try {
                    val user = userService.getUserById(userId)
                    authContext = AuthContext(user, principal)
                } catch (e: EntityNotFoundException) {
                    // Uživatel nebyl nalezen - autentizace selže
                    authContext = AuthContext()
                } catch (e: Exception) {
                    // Jiná chyba - autentizace selže
                    authContext = AuthContext()
                }
            }
        }

        // Pokud nemáme platného uživatele, vytvoříme prázdný kontext
        if (authContext == null) {
            authContext = AuthContext()
        }

        // Použijeme Builder pro přidání našeho kontextu do standardního GraphQLContext
        return GraphQLContext.newContext()
            .of("request", request)
            .of("authContext", authContext)
            .build()
    }
}