package config.di

import domain.ports.driven.JwtPort
import infrastructure.auth.JwtAdapter
import io.ktor.server.config.*
import org.koin.dsl.module

val authModule = module {
    single<JwtPort> {
        val config = getKoin().get<ApplicationConfig>()
        JwtAdapter(
            secret = config.property("jwt.secret").getString(),
            issuer = config.property("jwt.issuer").getString(),
            audience = config.property("jwt.audience").getString(),
            accessTokenLifetime = config.property("jwt.accessToken.lifetime").getString().toLong(),
            refreshTokenLifetime = config.property("jwt.refreshToken.lifetime").getString().toLong()
        )
    }
}