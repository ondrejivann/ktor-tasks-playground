package config.ktor

import domain.ports.TaskService
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ContactDirective
import com.expediagroup.graphql.server.Schema
import com.expediagroup.graphql.server.ktor.GraphQL
import infrastructure.graphql.TaskQueries
import infrastructure.graphql.TaskMutations
import io.ktor.server.application.*

fun Application.configureGraphQL(
    taskService: TaskService,
) {
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
    url = "https://myteam.slack.com/archives/teams-chat-room-url",
    description = "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall)."
)
@GraphQLDescription("My schema description")
class MySchema : Schema
