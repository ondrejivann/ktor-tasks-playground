package infrastructure.auth

import domain.ports.driven.JwtPort

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import domain.model.auth.TokenDetails
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*

class JwtAdapter(
    private val secret: String,
    private val issuer: String,
    private val audience: String,
    private val accessTokenLifetime: Long,
    private val refreshTokenLifetime: Long
) : JwtPort {

    private val algorithm = Algorithm.HMAC256(secret)
    private val logger = KotlinLogging.logger {}

    override fun generateAccessToken(userId: Int, email: String): String {
        logger.debug { "Generating access token for user $userId" }
        return JWT.create()
            .withSubject(userId.toString())
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("email", email)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenLifetime))
            .sign(algorithm)
    }

    override fun generateRefreshToken(userId: Int): String {
        logger.debug { "Generating refresh token for user $userId" }
        return JWT.create()
            .withSubject(userId.toString())
            .withIssuer(issuer)
            .withAudience(audience)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenLifetime))
            .sign(algorithm)
    }

    override fun verifyToken(token: String): TokenDetails? {
        return try {
            val verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()

            val decodedJWT = verifier.verify(token)
            val userId = decodedJWT.subject.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid user ID format in token")
            val email = decodedJWT.getClaim("email").asString()

            TokenDetails(
                userId = userId,
                email = email,
                issuedAt = decodedJWT.issuedAt.time,
                expiresAt = decodedJWT.expiresAt.time
            )
        } catch (e: JWTVerificationException) {
            logger.warn(e) { "JWT verification failed" }
            null
        } catch (e: IllegalArgumentException) {
            logger.warn(e) { "Invalid JWT format or claims" }
            null
        }
    }
}