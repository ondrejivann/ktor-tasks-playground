package domain.model.auth.command

import domain.model.auth.AuthProvider

data class RegisterUserCommand(
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val authProvider: AuthProvider = AuthProvider.LOCAL,
    val providerId: String? = null,
)
