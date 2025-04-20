package infrastructure.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("GraphQL Priority enum")
enum class PriorityGQL {
    LOW,
    MEDIUM,
    HIGH,
    VITAL,
}
