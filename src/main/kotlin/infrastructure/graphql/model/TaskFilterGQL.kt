package infrastructure.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Task filter")
data class TaskFilterGQL(
    @GraphQLDescription("Filter by priority")
    val priority: PriorityGQL? = null,

    @GraphQLDescription("Filter by task status")
    val statusCode: String? = null,

    @GraphQLDescription("Search in task name")
    val nameContains: String? = null,

    @GraphQLDescription("Search in task description")
    val descriptionContains: String? = null,

    @GraphQLDescription("Display just tasks containing text in name or description")
    val searchText: String? = null,

    @GraphQLDescription("Sorting results")
    val sortBy: TaskSortFieldGQL? = null,

    @GraphQLDescription("Sorting direction")
    val sortDirection: SortDirectionGQL? = SortDirectionGQL.ASC,
)

@GraphQLDescription("Field for result sorting")
enum class TaskSortFieldGQL {
    NAME,
    PRIORITY,
    STATUS,
}

@GraphQLDescription("Sorting direction")
enum class SortDirectionGQL {
    ASC,
    DESC,
}
