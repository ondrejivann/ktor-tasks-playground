package application.services

import domain.model.auth.RefreshToken
import domain.ports.driven.RefreshTokenRepository
import domain.ports.driving.RefreshTokenService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import java.time.LocalDateTime

@Single(binds = [RefreshTokenService::class])
class RefreshTokenServiceImpl(private val repository: RefreshTokenRepository) : RefreshTokenService {
    private val logger = KotlinLogging.logger {}

    override suspend fun createToken(userId: Int, token: String, expiresAt: LocalDateTime): RefreshToken {
        logger.debug { "Creating refresh token for user: $userId" }

        val refreshToken =
            RefreshToken(
                id = -1, // Temporary ID
                userId = userId,
                token = token,
                expiresAt = expiresAt,
                createdAt = LocalDateTime.now(),
            )

        return repository.save(refreshToken).also {
            logger.debug { "Refresh token created for user: $userId" }
        }
    }

    override suspend fun validateToken(token: String): Boolean {
        logger.debug { "Validating refresh token" }

        val refreshToken = repository.findByToken(token) ?: return false

        // Check if token is expired
        if (refreshToken.expiresAt.isBefore(LocalDateTime.now())) {
            logger.debug { "Token validation failed: Token expired" }
            // Automatically delete expired token
            repository.delete(refreshToken.id)
            return false
        }

        logger.debug { "Token validation successful for user: ${refreshToken.userId}" }
        return true
    }

    override suspend fun findByToken(token: String): RefreshToken? {
        logger.debug { "Finding refresh token" }
        return repository.findByToken(token)
    }

    override suspend fun invalidateToken(token: String): Boolean {
        logger.debug { "Invalidating refresh token" }

        val refreshToken = repository.findByToken(token) ?: return false

        return repository.delete(refreshToken.id).also {
            if (it) {
                logger.debug { "Token invalidated for user: ${refreshToken.userId}" }
            } else {
                logger.warn { "Failed to invalidate token for user: ${refreshToken.userId}" }
            }
        }
    }

    override suspend fun invalidateAllUserTokens(userId: Int): Int {
        logger.debug { "Invalidating all tokens for user: $userId" }

        return repository.invalidateAllForUser(userId).also {
            logger.debug { "Invalidated $it tokens for user: $userId" }
        }
    }

    override suspend fun cleanupExpiredTokens(): Int {
        logger.debug { "Cleaning up expired tokens" }

        return repository.deleteExpired().also {
            logger.debug { "Deleted $it expired tokens" }
        }
    }
}
