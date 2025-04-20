package infrastructure.graphql.mutations

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import domain.model.auth.AuthProvider
import domain.model.auth.command.RegisterUserCommand
import domain.ports.driving.UserService
import graphql.schema.DataFetchingEnvironment
import infrastructure.graphql.auth.AuthUtils
import infrastructure.graphql.auth.directive.RequireAuth
import infrastructure.graphql.model.auth.AuthResponseGQL
import infrastructure.graphql.model.auth.LoginInputGQL
import infrastructure.graphql.model.auth.RefreshTokenInputGQL
import infrastructure.graphql.model.auth.RegisterInputGQL
import infrastructure.graphql.model.auth.toGQL
import org.koin.core.annotation.Single

@Single
class AuthMutations(private val userService: UserService) {
    @GraphQLDescription("Register a new user")
    suspend fun register(input: RegisterInputGQL): AuthResponseGQL {
        val command = RegisterUserCommand(
            email = input.email,
            password = input.password,
            firstName = input.firstName,
            lastName = input.lastName,
            authProvider = AuthProvider.LOCAL,
        )

        val result = userService.registerUser(command)

        return AuthResponseGQL(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
            expiresIn = result.expiresIn.toInt(),
            user = result.user.toGQL(),
        )
    }

    @GraphQLDescription("Authenticate a user with email and password")
    suspend fun login(input: LoginInputGQL): AuthResponseGQL {
        val result = userService.authenticateUser(input.email, input.password)

        return AuthResponseGQL(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
            expiresIn = result.expiresIn.toInt(),
            user = result.user.toGQL(),
        )
    }

    @GraphQLDescription("Refresh an access token using a refresh token")
    suspend fun refreshToken(input: RefreshTokenInputGQL): AuthResponseGQL {
        val result = userService.refreshToken(input.refreshToken)

        return AuthResponseGQL(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
            expiresIn = result.expiresIn.toInt(),
            user = result.user.toGQL(),
        )
    }

    @RequireAuth
    @GraphQLDescription("Log out the current user by invalidating all refresh tokens")
    suspend fun logout(env: DataFetchingEnvironment): Boolean {
        val user = AuthUtils.getAuthenticatedUser(env)
        val invalidatedCount = userService.invalidateAllTokens(user.id)
        return invalidatedCount > 0
    }
}
