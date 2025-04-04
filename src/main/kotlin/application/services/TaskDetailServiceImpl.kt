package application.services

import common.exceptions.ErrorCodes
import domain.exceptions.EntityNotFoundException
import domain.filter.SortDirection
import domain.filter.TaskFilter
import domain.filter.TaskSortField
import domain.model.Priority
import domain.model.TaskAttachmentDetail
import domain.model.TaskDetail
import domain.model.command.CreateTaskCommand
import domain.model.command.CreateTaskCommandResponse
import domain.ports.driving.FileService
import domain.ports.driving.TaskAttachmentDetailService
import domain.ports.driving.TaskDetailService
import domain.ports.driving.TaskService
import infrastructure.rest.mapper.toPrepareTaskAttachmentUploadResponse
import org.koin.core.annotation.Single

@Single(binds = [TaskDetailService::class])
class TaskDetailServiceImpl(
    private val taskService: TaskService,
    private val taskAttachmentDetailService: TaskAttachmentDetailService,
    private val fileService: FileService,
) : TaskDetailService {
    override suspend fun getAllTasks(filter: TaskFilter?): List<TaskDetail> {
        val allTasks = if (filter?.priority != null) {
            taskService.tasksByPriority(filter.priority)
        } else {
            taskService.allTasks()
        }

        var filteredTasks = allTasks.map { task ->
            mapToTaskDetail(task)
        }

        // Apply filters
        if (filter != null) {
            if (filter.statusCode != null) {
                filteredTasks = filteredTasks.filter {
                    it.status.code == filter.statusCode
                }
            }

            if (filter.nameContains != null) {
                filteredTasks = filteredTasks.filter {
                    it.name.contains(filter.nameContains, ignoreCase = true)
                }
            }

            if (filter.descriptionContains != null) {
                filteredTasks = filteredTasks.filter {
                    it.description.contains(filter.descriptionContains, ignoreCase = true)
                }
            }

            if (filter.searchText != null) {
                filteredTasks = filteredTasks.filter { task ->
                    task.name.contains(filter.searchText, ignoreCase = true) ||
                            task.description.contains(filter.searchText, ignoreCase = true)
                }
            }

            // Apply sorting
            if (filter.sortBy != null) {
                filteredTasks = when (filter.sortBy) {
                    TaskSortField.NAME -> {
                        if (filter.sortDirection == SortDirection.ASC)
                            filteredTasks.sortedBy { it.name }
                        else
                            filteredTasks.sortedByDescending { it.name }
                    }
                    TaskSortField.PRIORITY -> {
                        if (filter.sortDirection == SortDirection.ASC)
                            filteredTasks.sortedBy { it.priority.ordinal }
                        else
                            filteredTasks.sortedByDescending { it.priority.ordinal }
                    }
                    TaskSortField.STATUS -> {
                        if (filter.sortDirection == SortDirection.ASC)
                            filteredTasks.sortedBy { it.status.code }
                        else
                            filteredTasks.sortedByDescending { it.status.code }
                    }
                }
            }
        }

        return filteredTasks
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

    override suspend fun getTaskById(id: Int): TaskDetail? {
        return taskService.taskById(id)?.let {
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
        val task = taskService.taskByName(name)
            ?: throw EntityNotFoundException("Task", name, errorCode = ErrorCodes.ENTITY_NOT_FOUND)

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