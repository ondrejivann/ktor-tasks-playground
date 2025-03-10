package config.ktor

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ContactDirective
import com.expediagroup.graphql.server.Schema
import com.expediagroup.graphql.server.ktor.GraphQL
import domain.ports.TaskService
import infrastructure.graphql.TaskMutations
import infrastructure.graphql.TaskQueries
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureGraphQL() {

    val taskService by inject<TaskService>()

    install(GraphQL) {
        schema {
            packages = listOf("infrastructure.graphql")
            queries = listOf(
                TaskQueries(taskService)
            )
            mutations = listOf(
                TaskMutations(taskService)
            )
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
