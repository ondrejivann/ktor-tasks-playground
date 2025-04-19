package config.ktor

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.federation.directives.ContactDirective
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.server.Schema
import com.expediagroup.graphql.server.ktor.GraphQL
import infrastructure.graphql.RootMutation
import infrastructure.graphql.RootQuery
import infrastructure.graphql.auth.GraphQLContextFactory
import infrastructure.graphql.auth.directive.AuthDirectiveWiringFactory
import infrastructure.graphql.exceptions.GraphQLExceptionHandler
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureGraphQL() {

    val rootQuery: RootQuery by inject()
    val rootMutation: RootMutation by inject()
    val customContextFactory: GraphQLContextFactory by inject()
    val authDirectiveWiringFactory = AuthDirectiveWiringFactory()

    val customHooks = object : SchemaGeneratorHooks {
        override val wiringFactory: KotlinDirectiveWiringFactory
            get() = authDirectiveWiringFactory
    }

    install(GraphQL) {

        schema {
            packages = listOf("infrastructure.graphql")
            queries = listOf(rootQuery)
            mutations = listOf(rootMutation)
            schemaObject = MySchema()
            hooks = customHooks
        }
        engine {
            exceptionHandler = GraphQLExceptionHandler()
        }
        server {
            contextFactory = customContextFactory
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
