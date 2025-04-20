@file:Suppress("ktlint:standard:no-empty-file")

package infrastructure.auth

//import domain.exceptions.AuthException
//import domain.model.auth.AuthProvider
//import domain.model.user.OAuthUserInfo
//import domain.ports.driven.OAuthPort
//import io.github.oshai.kotlinlogging.KotlinLogging
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.request.*
//import io.ktor.http.*
//import kotlinx.serialization.json.JsonObject
//import kotlinx.serialization.json.jsonPrimitive
//import org.koin.core.annotation.Single
//import common.exceptions.ErrorCodes
//import domain.model.auth.OAuthAccessToken
//import io.ktor.server.config.*

//@Single(binds = [OAuthPort::class])
//class OAuthAdapter(
//    private val httpClient: HttpClient,
//    private val applicationConfig: ApplicationConfig,
//) : OAuthPort {
//    private val logger = KotlinLogging.logger {}
//
//    override suspend fun getUserInfo(provider: AuthProvider, accessToken: String): OAuthUserInfo {
//        logger.debug { "Getting user info from provider: $provider" }
//
//        return when (provider) {
//            AuthProvider.GOOGLE -> getGoogleUserInfo(accessToken)
//            AuthProvider.LOCAL -> throw AuthException(
//                "Cannot get user info for LOCAL provider",
//                errorCode = ErrorCodes.AUTHENTICATION_ERROR
//            )
//            // Add other providers as needed
//        }
//    }
//
//    override suspend fun exchangeCodeForToken(
//        provider: AuthProvider,
//        code: String,
//        redirectUri: String
//    ): OAuthAccessToken {
//        logger.debug { "Exchanging code for token from provider: $provider" }
//
//        return when (provider) {
//            AuthProvider.GOOGLE -> exchangeGoogleCodeForToken(code, redirectUri)
//            AuthProvider.LOCAL -> throw AuthException(
//                "Cannot exchange code for LOCAL provider",
//                errorCode = ErrorCodes.AUTHENTICATION_ERROR
//            )
//            // Přidejte další poskytovatele podle potřeby
//        }
//    }
//
//    private suspend fun getGoogleUserInfo(accessToken: String): OAuthUserInfo {
//        try {
//            val response = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
//                headers {
//                    append(HttpHeaders.Authorization, "Bearer $accessToken")
//                }
//            }
//
//            if (response.status != HttpStatusCode.OK) {
//                logger.error { "Error from Google API: ${response.status}" }
//                throw AuthException(
//                    "Failed to get user info from Google: ${response.status}",
//                    errorCode = ErrorCodes.EXTERNAL_SERVICE_ERROR
//                )
//            }
//
//            val jsonObject = response.body<JsonObject>()
//
//            return OAuthUserInfo(
//                id = jsonObject["id"]?.jsonPrimitive?.content ?: "",
//                email = jsonObject["email"]?.jsonPrimitive?.content ?: "",
//                firstName = jsonObject["given_name"]?.jsonPrimitive?.content,
//                lastName = jsonObject["family_name"]?.jsonPrimitive?.content,
//                pictureUrl = jsonObject["picture"]?.jsonPrimitive?.content
//            )
//        } catch (e: Exception) {
//            logger.error(e) { "Error retrieving user info from Google: ${e.message}" }
//            throw AuthException(
//                "Failed to get user info from Google: ${e.message}",
//                cause = e,
//                errorCode = ErrorCodes.EXTERNAL_SERVICE_ERROR
//            )
//        }
//    }
//
//    private suspend fun exchangeGoogleCodeForToken(code: String, redirectUri: String): OAuthAccessToken {
//        try {
//            val clientId = applicationConfig.property("oauth.google.clientId").getString()
//            val clientSecret = applicationConfig.property("oauth.google.clientSecret").getString()
//
//            val response = httpClient.post("https://oauth2.googleapis.com/token") {
//                contentType(ContentType.Application.FormUrlEncoded)
//                setBody(Parameters.build {
//                    append("grant_type", "authorization_code")
//                    append("code", code)
//                    append("client_id", clientId)
//                    append("client_secret", clientSecret)
//                    append("redirect_uri", redirectUri)
//                })
//            }
//
//            if (response.status != HttpStatusCode.OK) {
//                logger.error { "Error from Google API: ${response.status}" }
//                throw AuthException(
//                    "Failed to exchange code for token: ${response.status}",
//                    errorCode = ErrorCodes.EXTERNAL_SERVICE_ERROR
//                )
//            }
//
//            val responseBody = response.body<JsonObject>()
//
//            return OAuthAccessToken(
//                accessToken = responseBody["access_token"]?.jsonPrimitive?.content
//                    ?: throw AuthException("Missing access_token in response"),
//                refreshToken = responseBody["refresh_token"]?.jsonPrimitive?.content,
//                idToken = responseBody["id_token"]?.jsonPrimitive?.content,
//                expiresIn = responseBody["expires_in"]?.jsonPrimitive?.content?.toLong() ?: 3600L,
//                tokenType = responseBody["token_type"]?.jsonPrimitive?.content ?: "Bearer"
//            )
//        } catch (e: Exception) {
//            logger.error(e) { "Error exchanging code for token: ${e.message}" }
//            throw AuthException(
//                "Failed to exchange code for token: ${e.message}",
//                cause = e,
//                errorCode = ErrorCodes.EXTERNAL_SERVICE_ERROR
//            )
//        }
//    }
//}
