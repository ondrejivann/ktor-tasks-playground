package com.example

//class FakeTaskRepository : TaskRepository {
//    private val tasks = mutableListOf(
//        Task("cleaning", "Clean the house", Priority.LOW),
//        Task("gardening", "Mow the lawn", Priority.MEDIUM),
//        Task("shopping", "Buy the groceries", Priority.HIGH),
//        Task("painting", "Paint the fence", Priority.MEDIUM)
//    )
//
//    override suspend fun allTasks(): List<Task> = tasks
//
//    override suspend fun tasksByPriority(priority: Priority) = tasks.filter {
//        it.priority == priority
//    }
//
//    override suspend fun taskByName(name: String) = tasks.find {
//        it.name.equals(name, ignoreCase = true)
//    }
//
//    override suspend fun addTask(task: Task) {
//        if (taskByName(task.name) != null) {
//            throw IllegalStateException("Cannot duplicate task names!")
//        }
//        tasks.add(task)
//    }
//
//    override suspend fun removeTask(name: String): Boolean {
//        return tasks.removeIf { it.name == name }
//    }
//}