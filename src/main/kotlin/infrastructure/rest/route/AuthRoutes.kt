package infrastructure.rest.route

import infrastructure.rest.controller.AuthController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.authRoutes(authController: AuthController) {

    route("/auth") {
        // Registrace nového uživatele
        post("/register") {
            authController.register(call)
        }

        // Přihlášení pomocí emailu a hesla
        post("/login") {
            authController.login(call)
        }

        // Obnova tokenu pomocí refresh tokenu
        post("/refresh") {
            authController.refreshToken(call)
        }

        // Endpointy vyžadující autentizaci
        authenticate("auth-jwt") {
            get("/me") {
                authController.getCurrentUser(call)
            }

            post("/logout") {
                authController.logout(call)
            }
        }
    }
}