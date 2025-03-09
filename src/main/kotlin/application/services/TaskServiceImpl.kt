package com.example.application.services

import com.example.domain.model.Priority
import com.example.domain.model.Task
import com.example.domain.ports.TaskRepository
import com.example.domain.ports.TaskService

class TaskServiceImpl(
    private val repository: TaskRepository
): TaskService {
    override suspend fun allTasks(): List<Task> {
        return repository.allTasks()
    }

    override suspend fun tasksByPriority(priority: Priority): List<Task> {
        return repository.tasksByPriority(priority)
    }

    override suspend fun taskByName(name: String): Task? {
        return repository.taskByName(name)
    }

    override suspend fun addTask(task: Task) {
        return repository.addTask(task)
    }

    override suspend fun removeTask(name: String): Boolean {
        return repository.removeTask(name)
    }
}