package config.ktor

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ContactDirective
import com.expediagroup.graphql.server.Schema
import com.expediagroup.graphql.server.ktor.GraphQL
import infrastructure.graphql.RootMutation
import infrastructure.graphql.RootQuery
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureGraphQL() {

    val rootQuery: RootQuery by inject()
    val rootMutation: RootMutation by inject()

    install(GraphQL) {
        schema {
            packages = listOf("infrastructure.graphql")
            queries = listOf(rootQuery)
            mutations = listOf(rootMutation)
            schemaObject = MySchema()
        }
    }
}

@ContactDirective(
    name = "My Team Name",
    url = "url to scheme",
    description = "This is my dream scheme"
)
@GraphQLDescription("My schema description")
class MySchema : Schema
