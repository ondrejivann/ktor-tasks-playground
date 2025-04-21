package infrastructure.graphql.model.auth

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Login input data")
data class LoginInputGQL(
    @GraphQLDescription("User email")
    val email: String,
    @GraphQLDescription("User password")
    val password: String,
)
