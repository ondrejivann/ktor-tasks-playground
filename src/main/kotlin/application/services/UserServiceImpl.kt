package application.services

import application.exceptions.BusinessRuleViolationException
import common.exceptions.ErrorCodes
import domain.exceptions.AuthException
import domain.exceptions.EntityNotFoundException
import domain.exceptions.ValidationException
import domain.model.auth.AuthProvider
import domain.model.auth.command.RegisterUserCommand
import domain.model.auth.response.AuthResponse
import domain.model.user.User
import domain.ports.driven.JwtPort
import domain.ports.driven.PasswordHashPort
import domain.ports.driven.UserRepository
import domain.ports.driving.RefreshTokenService
import domain.ports.driving.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.config.*
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single(binds = [UserService::class])
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val jwtPort: JwtPort,
    private val passwordHashPort: PasswordHashPort,
    // private val oAuthPort: OAuthPort,
    private val refreshTokenService: RefreshTokenService,
    config: ApplicationConfig,
) : UserService {
    private val logger = KotlinLogging.logger {}
    private val accessTokenLifetime = config.property("jwt.accessToken.lifetime").getString().toLong()

    override suspend fun registerUser(command: RegisterUserCommand): AuthResponse {
        logger.debug { "Registering new user: ${command.email}" }

        validateEmail(command.email)

        if (command.authProvider == AuthProvider.LOCAL) {
            validatePassword(command.password)
        }

        val existingUser = userRepository.findByEmail(command.email)
        if (existingUser != null) {
            logger.warn { "Registration failed: User with email ${command.email} already exists" }
            throw ValidationException(
                message = "User with this email already exists",
                errorCode = ErrorCodes.VALIDATION_ERROR
            )
        }

        val passwordHash = if (command.authProvider == AuthProvider.LOCAL) {
            passwordHashPort.hash(command.password)
        } else null

        val user = User(
            id = -1, // Temporary ID, will be replaced by database
            email = command.email,
            passwordHash = passwordHash,
            firstName = command.firstName,
            lastName = command.lastName,
            authProvider = command.authProvider,
            providerId = command.providerId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        try {
            val createdUser = userRepository.create(user)
            logger.info { "User ${createdUser.id} registered successfully" }

            val accessToken = jwtPort.generateAccessToken(createdUser.id, createdUser.email)
            val refreshToken = jwtPort.generateRefreshToken(createdUser.id)

            refreshTokenService.createToken(
                userId = createdUser.id,
                token = refreshToken,
                expiresAt = LocalDateTime.now().plusDays(30)
            )

            return AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = createdUser,
                expiresIn = accessTokenLifetime
            )
        } catch (e: Exception) {
            logger.error(e) { "Failed to register user: ${e.message}" }
            throw BusinessRuleViolationException(
                message = "Failed to register user: ${e.message}",
                cause = e,
                errorCode = ErrorCodes.BUSINESS_RULE_VIOLATION
            )
        }
    }

    override suspend fun authenticateUser(email: String, password: String): AuthResponse {
        logger.debug { "Authenticating user: $email" }

        val user = userRepository.findByEmail(email) ?: throw AuthException(
            message = "Invalid credentials",
            errorCode = ErrorCodes.INVALID_CREDENTIALS
        )

        if (user.authProvider != AuthProvider.LOCAL || user.passwordHash == null) {
            logger.warn { "Authentication failed: User ${user.id} uses external authentication" }
            throw AuthException(
                message = "This account uses external authentication",
                errorCode = ErrorCodes.INVALID_CREDENTIALS
            )
        }

        if (!passwordHashPort.verify(password, user.passwordHash)) {
            logger.warn { "Authentication failed: Invalid password for user ${user.id}" }
            throw AuthException(
                message = "Invalid credentials",
                errorCode = ErrorCodes.INVALID_CREDENTIALS
            )
        }

        logger.info { "User ${user.id} authenticated successfully" }

        val accessToken = jwtPort.generateAccessToken(user.id, user.email)
        val refreshToken = jwtPort.generateRefreshToken(user.id)

        refreshTokenService.createToken(
            userId = user.id,
            token = refreshToken,
            expiresAt = LocalDateTime.now().plusDays(30)
        )

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user,
            expiresIn = accessTokenLifetime
        )
    }

    override suspend fun refreshToken(token: String): AuthResponse {
        logger.debug { "Refreshing token" }

        val refreshTokenObj = refreshTokenService.findByToken(token) ?: throw AuthException(
            message = "Invalid refresh token",
            errorCode = ErrorCodes.INVALID_TOKEN
        )

        if (refreshTokenObj.expiresAt.isBefore(LocalDateTime.now())) {
            logger.warn { "Token refresh failed: Token expired" }
            throw AuthException(
                message = "Refresh token expired",
                errorCode = ErrorCodes.TOKEN_EXPIRED
            )
        }

        val user = userRepository.findById(refreshTokenObj.userId) ?: throw EntityNotFoundException(
            entity = "User",
            identifier = refreshTokenObj.userId
        )

        // Invalidate old token
        refreshTokenService.invalidateToken(token)

        // Generate new tokens
        val accessToken = jwtPort.generateAccessToken(user.id, user.email)
        val newRefreshToken = jwtPort.generateRefreshToken(user.id)

        refreshTokenService.createToken(
            userId = user.id,
            token = newRefreshToken,
            expiresAt = LocalDateTime.now().plusDays(30)
        )

        logger.info { "Token refreshed successfully for user ${user.id}" }

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = newRefreshToken,
            user = user,
            expiresIn = accessTokenLifetime
        )
    }

    override suspend fun getUserById(id: Int): User {
        logger.debug { "Getting user by ID: $id" }
        return userRepository.findById(id) ?: throw EntityNotFoundException(
            entity = "User",
            identifier = id
        )
    }

    override suspend fun getUserByEmail(email: String): User? {
        logger.debug { "Getting user by email: $email" }
        return userRepository.findByEmail(email)
    }

//    override suspend fun getUserInfoFromGoogle(accessToken: String): OAuthUserInfo {
//        logger.debug { "Getting user info from Google using OAuthPort" }
//
//        try {
//            return oAuthPort.getUserInfo(AuthProvider.GOOGLE, accessToken)
//        } catch (e: Exception) {
//            logger.error(e) { "Failed to get user info from Google: ${e.message}" }
//            throw BusinessRuleViolationException(
//                message = "Failed to get user info from Google",
//                cause = e,
//                errorCode = ErrorCodes.EXTERNAL_SERVICE_ERROR
//            )
//        }
//    }

//    override suspend fun processOAuthCode(
//        provider: AuthProvider,
//        authorizationCode: String,
//        redirectUri: String
//    ): AuthResponse {
//        logger.debug { "Processing OAuth code for provider: $provider" }
//
//        // 1. Výměna kódu za tokeny
//        val oauthToken = oAuthPort.exchangeCodeForToken(provider, authorizationCode, redirectUri)
//
//        // 2. Autentizace uživatele pomocí access tokenu
//        return authenticateWithOAuth(provider, oauthToken.accessToken)
//    }

//    override suspend fun authenticateWithOAuth(provider: AuthProvider, accessToken: String): AuthResponse {
//        logger.debug { "Authenticating with OAuth: provider: $provider" }
//
//        val userInfo = oAuthPort.getUserInfo(provider, accessToken)
//
//        if (userInfo.email.isBlank()) {
//            throw ValidationException(
//                message = "Email is required from OAuth provider",
//                errorCode = ErrorCodes.VALIDATION_ERROR
//            )
//        }
//
//        var user = userRepository.findByProviderId(userInfo.id, provider)
//
//        if (user == null) {
//            // Check if user with this email exists
//            val existingUser = userRepository.findByEmail(userInfo.email)
//
//            user = if (existingUser != null) {
//                // Update existing user with OAuth info
//                val updatedUser = existingUser.copy(
//                    authProvider = provider,
//                    providerId = userInfo.id,
//                    firstName = userInfo.firstName ?: existingUser.firstName,
//                    lastName = userInfo.lastName ?: existingUser.lastName,
//                    updatedAt = LocalDateTime.now()
//                )
//                userRepository.update(updatedUser)
//            } else {
//                // Create new user
//                val newUser = User(
//                    id = -1, // Temporary ID
//                    email = userInfo.email,
//                    passwordHash = null,
//                    firstName = userInfo.firstName,
//                    lastName = userInfo.lastName,
//                    authProvider = provider,
//                    providerId = userInfo.id,
//                    createdAt = LocalDateTime.now(),
//                    updatedAt = LocalDateTime.now()
//                )
//                userRepository.create(newUser)
//            }
//        }
//
//        val newAccessToken = jwtPort.generateAccessToken(user.id, user.email)
//        val refreshToken = jwtPort.generateRefreshToken(user.id)
//
//        refreshTokenService.createToken(
//            userId = user.id,
//            token = refreshToken,
//            expiresAt = LocalDateTime.now().plusDays(30)
//        )
//
//        logger.info { "User ${user.id} authenticated successfully with OAuth" }
//
//        return AuthResponse(
//            accessToken = newAccessToken,
//            refreshToken = refreshToken,
//            user = user,
//            expiresIn = accessTokenLifetime
//        )
//    }

    override suspend fun invalidateAllTokens(userId: Int): Int {
        logger.debug { "Invalidating all tokens for user: $userId" }
        return refreshTokenService.invalidateAllUserTokens(userId)
    }

    private fun validateEmail(email: String) {
        if (email.isBlank()) {
            throw ValidationException(
                message = "Email cannot be empty",
                errorCode = ErrorCodes.VALIDATION_ERROR
            )
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)$")
        if (!emailRegex.matches(email)) {
            throw ValidationException(
                message = "Invalid email format",
                errorCode = ErrorCodes.VALIDATION_ERROR
            )
        }
    }

    private fun validatePassword(password: String) {
        if (password.isBlank()) {
            throw ValidationException(
                message = "Password cannot be empty",
                errorCode = ErrorCodes.VALIDATION_ERROR
            )
        }

        if (password.length < 8) {
            throw ValidationException(
                message = "Password must be at least 8 characters long",
                errorCode = ErrorCodes.VALIDATION_ERROR
            )
        }
    }
}