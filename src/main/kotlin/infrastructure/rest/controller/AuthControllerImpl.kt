package infrastructure.rest.controller

import UserProfileResponse
import domain.model.auth.AuthProvider
import domain.model.auth.UserSession
import domain.model.auth.command.RegisterUserCommand
import domain.ports.driving.UserService
import infrastructure.rest.dto.LoginRequest
import infrastructure.rest.dto.RefreshTokenRequest
import infrastructure.rest.dto.RegisterRequest
import infrastructure.rest.dto.TokenResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import org.koin.core.annotation.Single

@Single(binds = [AuthController::class])
class AuthControllerImpl(
    private val userService: UserService,
    // private val applicationConfig: ApplicationConfig
) : AuthController {
    private val logger = KotlinLogging.logger {}
    override suspend fun register(call: ApplicationCall) {
        logger.debug { "Processing registration request" }

        val request = call.receive<RegisterRequest>()

        val command = RegisterUserCommand(
            email = request.email,
            password = request.password,
            firstName = request.firstName,
            lastName = request.lastName,
            authProvider = AuthProvider.LOCAL,
        )

        val result = userService.registerUser(command)

        call.respond(
            HttpStatusCode.Created,
            TokenResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                expiresIn = result.expiresIn,
                userId = result.user.id,
                email = result.user.email,
            ),
        )
    }

    override suspend fun login(call: ApplicationCall) {
        logger.debug { "Processing login request" }

        val request = call.receive<LoginRequest>()

        val result = userService.authenticateUser(request.email, request.password)

        call.respond(
            status = HttpStatusCode.OK,
            message = TokenResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                expiresIn = result.expiresIn,
                userId = result.user.id,
                email = result.user.email,
            ),
        )
    }

    override suspend fun refreshToken(call: ApplicationCall) {
        logger.debug { "Processing token refresh request" }

        val request = call.receive<RefreshTokenRequest>()

        val result = userService.refreshToken(request.refreshToken)

        call.respond(
            status = HttpStatusCode.OK,
            message = TokenResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                expiresIn = result.expiresIn,
                userId = result.user.id,
                email = result.user.email,
            ),
        )
    }

    override suspend fun getCurrentUser(call: ApplicationCall) {
        logger.debug { "Getting current user profile" }

        val principal = call.principal<JWTPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized)

        val userId = principal.payload.getClaim("sub").asString().toIntOrNull()
            ?: return call.respond(HttpStatusCode.Unauthorized)

        val user = userService.getUserById(userId)

        call.respond(
            status = HttpStatusCode.OK,
            message = UserProfileResponse(
                id = user.id,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                authProvider = user.authProvider,
            ),
        )
    }

    override suspend fun logout(call: ApplicationCall) {
        logger.debug { "Processing logout request" }

        val principal = call.principal<JWTPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized)

        val userId = principal.payload.getClaim("sub").asString().toIntOrNull()
            ?: return call.respond(HttpStatusCode.Unauthorized)

        userService.invalidateAllTokens(userId)

        call.sessions.clear<UserSession>()

        call.respond(
            status = HttpStatusCode.OK,
            message = "Logout success",
        )
    }

//    private fun buildRedirectUri(call: ApplicationCall): String {
//        // Ideálně použijte konfigurační hodnotu z application.yaml
//        return try {
//            applicationConfig.property("oauth.google.redirectUri").getString()
//        } catch (e: Exception) {
//            // Fallback na dynamické vytvoření
//            val scheme = call.request.origin.scheme
//            val host = call.request.host()
//            val serverPort = call.request.origin.serverPort
//
//            // Přidáme port do URL jen pokud není standardní port (80 pro HTTP, 443 pro HTTPS)
//            val portPart = if (serverPort == 80 || serverPort == 443) "" else ":$serverPort"
//
//            "$scheme://$host$portPart/auth/callback"
//        }
//    }
}
