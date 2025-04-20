package infrastructure.graphql.auth.directive

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import domain.exceptions.AuthException
import graphql.schema.DataFetcherFactories
import graphql.schema.GraphQLFieldDefinition
import infrastructure.graphql.auth.AuthContext

/**
 * Implementation of the authentication directive that verifies if the user is authenticated.
 * This class handles the runtime behavior of the @requireAuth directive.
 */
class RequireAuthDirectiveWiring : KotlinSchemaDirectiveWiring {
    /**
     * Called when processing a field with the @requireAuth directive.
     * Adds authentication check to the field's data fetcher.
     *
     * @param environment The field directive environment
     * @return The modified GraphQL field definition
     */
    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher = environment.getDataFetcher()

        // Vytvoříme nový DataFetcher, který ověří autentizaci před zavoláním původního DataFetcheru
        val authCheckingFetcher = DataFetcherFactories.wrapDataFetcher(
            originalDataFetcher,
            { dataEnv, value ->
                val authContext = dataEnv.graphQlContext.get<AuthContext>("authContext")
                if (authContext == null || !authContext.isAuthenticated) {
                    throw AuthException("Authentication required")
                }
                // Pokud je autentizace úspěšná, vrátíme původní hodnotu
                value
            },
        )

        // Nastavíme upravený DataFetcher
        environment.setDataFetcher(authCheckingFetcher)

        // Vrátíme původní definici pole
        return field
    }
}
