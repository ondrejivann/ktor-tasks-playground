package domain.filter

import domain.model.Priority

data class TaskFilter(
    val priority: Priority? = null,
    val statusCode: String? = null,
    val nameContains: String? = null,
    val descriptionContains: String? = null,
    val searchText: String? = null,
    val sortBy: TaskSortField? = null,
    val sortDirection: SortDirection? = SortDirection.ASC
)

enum class TaskSortField {
    NAME,
    PRIORITY,
    STATUS
}

enum class SortDirection {
    ASC,
    DESC
}
