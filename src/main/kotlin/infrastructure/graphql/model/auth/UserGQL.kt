package infrastructure.graphql.model.auth

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import domain.model.auth.AuthProvider
import domain.model.user.User

@GraphQLDescription("User profile information")
data class UserGQL(
    @GraphQLDescription("User ID")
    val id: Int,

    @GraphQLDescription("User email")
    val email: String,

    @GraphQLDescription("User first name")
    val firstName: String?,

    @GraphQLDescription("User last name")
    val lastName: String?,

    @GraphQLDescription("Authentication provider (LOCAL, GOOGLE, etc.)")
    val authProvider: AuthProvider
)

// Extension function to convert domain User to UserGQL
fun User.toGQL(): UserGQL = UserGQL(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    authProvider = authProvider
)