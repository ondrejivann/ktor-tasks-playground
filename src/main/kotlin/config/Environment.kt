package config

enum class Environment(val envValue: String) {
    DEVELOPMENT(envValue = "dev"),
    PRODUCTION(envValue = "prod"),
    ;

    companion object {
        private const val ENV_VAR_NAME = "KTOR_ENV"

        val current: Environment by lazy {
            when (System.getenv(ENV_VAR_NAME) ?: System.getProperty(ENV_VAR_NAME)?.uppercase()) {
                "PROD", "PRODUCTION" -> PRODUCTION
                "DEV", "DEVELOPMENT" -> DEVELOPMENT
                else -> {
                    // Pokud proměnná není nastavena, defaultně do DEV
                    println("⚠️ WARNING: $ENV_VAR_NAME not set. Defaulting to DEVELOPMENT.")
                    DEVELOPMENT
                }
            }
        }

        val isDevelopment: Boolean
            get() = current == DEVELOPMENT

        val isProduction: Boolean
            get() = current == PRODUCTION
    }
}
