import domain.model.auth.AuthProvider
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val id: Int,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val authProvider: AuthProvider
)