package infrastructure.graphql.model.auth

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Authentication response containing tokens and user information")
data class AuthResponseGQL(
    @GraphQLDescription("JWT access token")
    val accessToken: String,

    @GraphQLDescription("Refresh token for obtaining new access tokens")
    val refreshToken: String,

    @GraphQLDescription("Time in seconds until the access token expires")
    val expiresIn: Int,

    @GraphQLDescription("Authenticated user information")
    val user: UserGQL,
)
