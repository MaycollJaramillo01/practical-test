package Controller

import Data.ITaskDataManager
import Data.MemoryTaskDataManager
import Entity.Task
import android.content.Context
import cr.ac.utn.practicaltest.R

class TaskController(context: Context) {
    private val dataManager: ITaskDataManager = MemoryTaskDataManager
    private val appContext = context.applicationContext

    fun addTask(task: Task) {
        if (dataManager.getById(task.id) != null) {
            throw Exception(appContext.getString(R.string.MsgDuplicateDate))
        }
        try {
            dataManager.add(task)
        } catch (e: Exception) {
            throw Exception(appContext.getString(R.string.ErrorMsgAdd))
        }
    }

    fun updateTask(task: Task) {
        if (dataManager.getById(task.id) == null) {
            throw Exception(appContext.getString(R.string.MsgDataNoFound))
        }
        try {
            dataManager.update(task)
        } catch (e: Exception) {
            throw Exception(appContext.getString(R.string.ErrorMsgUpdate))
        }
    }

    fun removeTask(id: String) {
        val current = dataManager.getById(id)
        if (current == null) {
            throw Exception(appContext.getString(R.string.MsgDataNoFound))
        }
        try {
            dataManager.remove(id)
        } catch (e: Exception) {
            throw Exception(appContext.getString(R.string.ErrorMsgRemove))
        }
    }

    fun getTaskById(id: String): Task? = try {
        dataManager.getById(id)
    } catch (e: Exception) {
        throw Exception(appContext.getString(R.string.ErrorMsgGetById))
    }

    fun getTasks(): List<Task> = try {
        dataManager.getAll()
    } catch (e: Exception) {
        throw Exception(appContext.getString(R.string.ErrorMsgGetAll))
    }
}
