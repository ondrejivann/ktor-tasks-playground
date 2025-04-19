package infrastructure.graphql

import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.graphQLRoutes() {
    authenticate("auth-jwt", optional = true) {
        graphQLPostRoute()
        graphQLGetRoute()
    }
}