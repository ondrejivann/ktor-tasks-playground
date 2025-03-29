package application.services

import domain.model.Priority
import domain.model.TaskAttachmentDetail
import domain.model.TaskDetail
import domain.model.command.CreateTaskCommand
import domain.model.command.CreateTaskCommandResponse
import domain.ports.driving.*
import infrastructure.rest.mapper.toPrepareTaskAttachmentUploadResponse
import org.koin.core.annotation.Single

@Single(binds = [TaskDetailService::class])
class TaskDetailServiceImpl(
    private val taskService: TaskService,
    private val taskAttachmentDetailService: TaskAttachmentDetailService,
    private val fileService: FileService,
) : TaskDetailService {
    override suspend fun getAllTasks(): List<TaskDetail> {
        return taskService.allTasks().map {
            mapToTaskDetail(it)
        }
    }

    override suspend fun getTaskDetailByPriority(priority: Priority): List<TaskDetail> {
        return taskService.tasksByPriority(priority).map {
            mapToTaskDetail(it)
        }
    }

    override suspend fun getTaskByName(name: String): TaskDetail? {
        return taskService.taskByName(name)?.let {
            mapToTaskDetail(it)
        }
    }

    override suspend fun addTask(command: CreateTaskCommand): CreateTaskCommandResponse {
        val newTask = taskService.addTask(command)

        val taskAttachmentUploads = command.attachments.map { attachmentRequest ->
            val fileUploadInfo = taskAttachmentDetailService.prepareUploadForTask(
                taskId = newTask.id,
                fileName = attachmentRequest.fileName,
                contentType = attachmentRequest.contentType,
                fileSize = attachmentRequest.fileSize,
            )
            fileUploadInfo.toPrepareTaskAttachmentUploadResponse()
        }

        return CreateTaskCommandResponse(
            id = newTask.id,
            name = newTask.name,
            description = newTask.description,
            status = newTask.status,
            priority = newTask.priority,
            attachments = taskAttachmentUploads,
        )
    }

    override suspend fun removeTask(name: String): Boolean {
        val task = taskService.taskByName(name) ?: return false

        task.attachments.forEach { attachment ->
            taskAttachmentDetailService.removeAttachment(attachment.id)
        }

        return taskService.removeTask(name)
    }

    override suspend fun updateTaskStatus(name: String, statusCode: String): Boolean {
        return taskService.updateTaskStatus(name, statusCode)
    }

    private suspend fun mapToTaskDetail(task: domain.model.Task): TaskDetail {
        val attachments = task.attachments.map { attachment ->
            val fileDownloadInfo = fileService.generateDownloadLink(attachment.fileKey)
            TaskAttachmentDetail(
                id = attachment.id,
                fileKey = attachment.fileKey,
                fileName = attachment.fileName,
                contentType = attachment.contentType,
                downloadUrl = fileDownloadInfo.downloadUrl
            )
        }

        return TaskDetail(
            id = task.id,
            name = task.name,
            description = task.description,
            priority = task.priority,
            status = task.status,
            attachments = attachments
        )
    }
}