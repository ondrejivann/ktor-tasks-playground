package infrastructure.graphql

import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import io.ktor.server.routing.*

fun Route.graphQLRoutes() {
    graphQLPostRoute()
    graphQLGetRoute()
}