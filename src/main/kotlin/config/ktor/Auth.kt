package config.ktor

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import domain.exceptions.AuthException
import domain.model.auth.UserSession
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.sessions.*

fun Application.configureAuth() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    val accessTokenLifetime = environment.config.property("jwt.accessToken.lifetime").getString().toLong()
    val refreshTokenLifetime = environment.config.property("jwt.refreshToken.lifetime").getString().toLong()

    val clientId = environment.config.property("oauth.google.clientId").getString()
    val clientSecret = environment.config.property("oauth.google.clientSecret").getString()

    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.httpOnly = true
            cookie.secure = true
        }
    }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                throw AuthException("Token is not valid or has expired")
            }
        }

        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = clientId,
                    clientSecret = clientSecret,
                    defaultScopes = listOf("email", "profile")
                )
            }
            client = HttpClient(Apache)
        }
    }
}