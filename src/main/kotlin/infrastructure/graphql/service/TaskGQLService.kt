package infrastructure.graphql.service

import domain.model.Task
import domain.ports.driven.FileStoragePort
import domain.ports.driving.TaskAttachmentService
import domain.ports.driven.TaskRepository
import infrastructure.graphql.model.TaskGQL
import infrastructure.graphql.model.toGQL
import infrastructure.graphql.toGQL
import org.koin.core.annotation.Single

@Single
class TaskGraphQLService(
    private val taskRepository: TaskRepository,
    private val taskAttachmentService: TaskAttachmentService,
    private val fileStorageService: FileStoragePort
) {
    suspend fun getAllTasks(): List<TaskGQL> {
        val tasks = taskRepository.allTasks()
        return tasks.map { task -> mapTaskWithAttachments(task) }
    }

    suspend fun getTaskById(id: Int): TaskGQL? {
        val task = taskRepository.taskById(id) ?: return null
        return mapTaskWithAttachments(task)
    }

    private suspend fun mapTaskWithAttachments(task: Task): TaskGQL {
        val taskGQL = task.toGQL()

        val attachments = taskAttachmentService.getAttachmentsForTask(task.id)

        val attachmentsGQL = attachments.map { attachment ->
            val downloadUrl = fileStorageService.generateDownloadUrl(attachment.fileKey)
            attachment.toGQL(downloadUrl)
        }

        return taskGQL.copy(attachments = attachmentsGQL)
    }
}