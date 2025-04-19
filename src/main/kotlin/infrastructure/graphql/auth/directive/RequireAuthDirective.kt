package infrastructure.graphql.auth.directive

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import graphql.introspection.Introspection

/**
 * Directive for requiring authentication on GraphQL operations.
 * This directive can be applied to fields that require an authenticated user.
 */
@GraphQLDirective(
    name = "requireAuth",
    description = "Marks operations requiring authentication",
    locations = [Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.FIELD]
)
@GraphQLDescription("Marks operations requiring authentication")
annotation class RequireAuth