package infrastructure.graphql.mutations

import org.koin.core.annotation.Single

@Single
class TaskAppMutations(private val taskMutations: TaskMutations, private val taskAttachmentMutations: TaskAttachmentMutations) {
    fun tasks(): TaskMutations = taskMutations
    fun attachments(): TaskAttachmentMutations = taskAttachmentMutations
}
