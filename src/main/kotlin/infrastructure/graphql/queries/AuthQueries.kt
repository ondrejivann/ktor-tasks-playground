package infrastructure.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import graphql.schema.DataFetchingEnvironment
import infrastructure.graphql.auth.AuthUtils
import infrastructure.graphql.auth.directive.RequireAuth
import infrastructure.graphql.model.auth.UserGQL
import infrastructure.graphql.model.auth.toGQL
import org.koin.core.annotation.Single

@Single
class AuthQueries {
    @RequireAuth
    @GraphQLDescription("Retrieve information about the currently authenticated user")
    fun me(env: DataFetchingEnvironment): UserGQL {
        val user = AuthUtils.getAuthenticatedUser(env)
        return user.toGQL()
    }
}