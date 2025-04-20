package infrastructure.graphql.model.auth

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("User registration input data")
data class RegisterInputGQL(
    @GraphQLDescription("User email")
    val email: String,

    @GraphQLDescription("User password")
    val password: String,

    @GraphQLDescription("User first name")
    val firstName: String?,

    @GraphQLDescription("User last name")
    val lastName: String?,
)
