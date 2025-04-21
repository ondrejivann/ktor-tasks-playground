package infrastructure.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import domain.ports.driven.PasswordHashPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

/**
 * Implementation of PasswordHashPort using the BCrypt algorithm.
 *
 * BCrypt is a password hashing function based on the Blowfish cipher.
 * It incorporates a salt to protect against rainbow table attacks
 * and is adaptive, meaning the work factor can be increased to
 * keep up with hardware improvements.
 */
@Single(binds = [PasswordHashPort::class])
class PasswordHashAdapter : PasswordHashPort {
    private val logger = KotlinLogging.logger {}

    // BCrypt cost parameter (4-31, OWASP recommends at least 10)
    companion object {
        private const val COST = 12
    }

    override fun hash(password: String): String {
        logger.debug { "Hashing password with BCrypt" }

        // BCrypt.withDefaults() uses BCrypt.Version.VERSION_2A ($2a$)
        return BCrypt.withDefaults().hashToString(
            COST,
            password.toCharArray(),
        )
    }

    override fun verify(password: String, hash: String): Boolean {
        logger.debug { "Verifying password with BCrypt" }

        try {
            val result =
                BCrypt.verifyer().verify(
                    password.toCharArray(),
                    hash,
                )

            return result.verified
        } catch (e: Exception) {
            logger.error(e) { "Error verifying password: ${e.message}" }
            return false
        }
    }
}
