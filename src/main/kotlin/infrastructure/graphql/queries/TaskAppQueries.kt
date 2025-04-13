package infrastructure.graphql.queries

import org.koin.core.annotation.Single

@Single
class TaskAppQueries(
    private val taskQueries: TaskQueries,
    private val taskAttachmentQueries: TaskAttachmentQueries,
) {
    fun tasks(): TaskQueries = taskQueries
    fun taskAttachments(): TaskAttachmentQueries = taskAttachmentQueries
}