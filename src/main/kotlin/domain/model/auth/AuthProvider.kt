package domain.model.auth

/**
 * Defines the authentication providers supported by the system.
 *
 * This enumeration represents the different methods through which users
 * can authenticate with the application, including both local authentication
 * and various OAuth providers.
 *
 * @property LOCAL Authentication with email and password managed by the application
 * @property GOOGLE Authentication through Google OAuth 2.0
 */
enum class AuthProvider {
    /**
     * Local authentication using email and password managed by the application.
     * Users with this provider will have a non-null passwordHash.
     */
    LOCAL,

    /**
     * Authentication through Google OAuth 2.0.
     * Users with this provider will have null passwordHash and non-null providerId.
     */
    GOOGLE,
}
