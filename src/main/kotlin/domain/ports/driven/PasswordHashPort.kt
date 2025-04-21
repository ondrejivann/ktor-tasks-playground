package domain.ports.driven

/**
 * Port defining operations for password hashing and verification.
 *
 * This port is responsible for securely hashing passwords and verifying
 * password hashes following cryptographic best practices.
 */
interface PasswordHashPort {
    /**
     * Creates a secure hash of the provided password.
     *
     * @param password The password to hash
     * @return A string representation of the hashed password in a format that includes
     *         all parameters needed for verification (typically using a standard format)
     */
    fun hash(password: String): String

    /**
     * Verifies a password against a stored hash.
     *
     * @param password The password to verify
     * @param hash The stored password hash
     * @return true if the password matches the hash, false otherwise
     */
    fun verify(password: String, hash: String): Boolean
}
