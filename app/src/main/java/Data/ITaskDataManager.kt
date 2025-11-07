package Data

import Entity.Task

interface ITaskDataManager {
    fun add(task: Task)
    fun update(task: Task)
    fun remove(id: String)
    fun getAll(): List<Task>
    fun getById(id: String): Task?
}
