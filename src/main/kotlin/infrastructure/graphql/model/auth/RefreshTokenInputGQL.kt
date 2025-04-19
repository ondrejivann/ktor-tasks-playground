package infrastructure.graphql.model.auth

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Refresh token input")
data class RefreshTokenInputGQL(
    @GraphQLDescription("Refresh token to exchange for a new access token")
    val refreshToken: String
)