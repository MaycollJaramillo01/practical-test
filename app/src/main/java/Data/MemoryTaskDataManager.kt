package Data

import Entity.Task
import Entity.TaskStatus
import java.time.LocalDate

object MemoryTaskDataManager : ITaskDataManager {
    private val taskList = mutableListOf(
        Task(
            id = "T-001",
            title = "Revisión de requerimientos",
            description = "Analizar los requerimientos recibidos del cliente.",
            assignedTo = "Ana Gómez",
            dueDate = LocalDate.now().plusDays(2),
            status = TaskStatus.IN_PROGRESS
        ),
        Task(
            id = "T-002",
            title = "Actualización de documentación",
            description = "Actualizar la documentación del módulo de autenticación.",
            assignedTo = "Luis Rodríguez",
            dueDate = LocalDate.now().plusDays(5),
            status = TaskStatus.PENDING
        )
    )

    override fun add(task: Task) {
        taskList.add(task)
    }

    override fun update(task: Task) {
        remove(task.id)
        add(task)
    }

    override fun remove(id: String) {
        taskList.removeIf { it.id.trim() == id.trim() }
    }

    override fun getAll(): List<Task> = taskList.toList()

    override fun getById(id: String): Task? = taskList.firstOrNull { it.id.trim() == id.trim() }
}
