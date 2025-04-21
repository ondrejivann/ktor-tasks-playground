@file:Suppress("ktlint:standard:no-empty-file")

package application.services

// /**
// * Manages temporary codes used for secure token exchange.
// */
// @Single
// class TempCodeManager {
//    private val logger = KotlinLogging.logger {}
//    private val codeMap = ConcurrentHashMap<String, Pair<AuthResponse, Long>>()
//    private val expirationTimeMs = 300_000L // 5 minut
//
//    init {
//        // Nastavení pravidelného čištění vypršených kódů
//        val scheduler = Executors.newSingleThreadScheduledExecutor()
//        scheduler.scheduleAtFixedRate(
//            { cleanupExpiredCodes() },
//            5, 5, TimeUnit.MINUTES
//        )
//    }
//
//    fun generateTempCode(authResponse: AuthResponse): String {
//        val code = UUID.randomUUID().toString()
//        codeMap[code] = Pair(authResponse, System.currentTimeMillis() + expirationTimeMs)
//        logger.debug { "Generated temporary code: $code" }
//        return code
//    }
//
//    fun getAuthResponse(code: String): AuthResponse? {
//        val entry = codeMap[code] ?: return null
//        logger.debug { "Found entry for code: $code" }
//
//        // Ověřte, zda kód nevypršel
//        if (System.currentTimeMillis() > entry.second) {
//            logger.debug { "Code expired: $code" }
//            codeMap.remove(code)
//            return null
//        }
//
//        // Odstraňte kód po použití
//        codeMap.remove(code)
//        logger.debug { "Removed used code: $code" }
//        return entry.first
//    }
//
//    private fun cleanupExpiredCodes() {
//        val currentTime = System.currentTimeMillis()
//        val initialSize = codeMap.size
//
//        codeMap.entries.removeIf { entry -> currentTime > entry.value.second }
//
//        val removedCount = initialSize - codeMap.size
//        if (removedCount > 0) {
//            logger.debug { "Cleaned up $removedCount expired temporary codes" }
//        }
//    }
// }
