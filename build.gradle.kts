
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.google.devtools.ksp)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.postgresql)
    implementation(libs.h2)

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.time)


    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors)

    // Swagger and OpenAPI
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.openapi)

    // GraphQL
    implementation(libs.graphql.kotlin.ktor.server)

    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)

    implementation(libs.ktor.server.call.logging)
    implementation(libs.kotlin.logging)
    
    // Koin for Ktor
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

    // Flyway migrations
    implementation(libs.flywaydb.flyway.core)
    implementation(libs.flywaydb.flyway.database.postgresql)

    // Hikari pool
    implementation(libs.hikari.pool)

    // AWS S3
    implementation(libs.aws.sdk.kotlin)

    // Dotenv
    implementation(libs.dotenv.kotlin)

    // Auth
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.sessions)
    implementation(libs.jwt.auth0)

    // OAuth client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.apache)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.jayway.jsonpath)
}
