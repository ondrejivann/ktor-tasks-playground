package infrastructure.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import domain.model.Priority

@GraphQLDescription("Creation of Task Model")
data class CreateTaskCommandGQL(
    val name: String,
    val description: String,
    val priority: Priority
)