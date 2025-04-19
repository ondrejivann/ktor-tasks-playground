package infrastructure.graphql.auth.directive

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveEnvironment
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.schema.GraphQLDirectiveContainer

/**
 * Factory for creating GraphQL directives.
 * Registers the authentication directive in the GraphQL schema.
 */
class AuthDirectiveWiringFactory : KotlinDirectiveWiringFactory() {
    /**
     * Returns a schema directive wiring for the given environment.
     * Used to associate directive names with their implementations.
     *
     * @param environment The schema directive environment
     * @return The directive wiring implementation or null if not supported
     */
    override fun getSchemaDirectiveWiring(environment: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): KotlinSchemaDirectiveWiring? {
        val directive = environment.directive
        val element = environment.element

        return when {
            directive.name == "requireAuth" -> RequireAuthDirectiveWiring()
            else -> null
        }
    }
}