package cr.ac.utn.practicaltest

import Controller.TaskController
import Entity.Task
import Entity.TaskStatus
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import cr.ac.utn.practicaltest.task.TaskAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TaskActivity : AppCompatActivity() {

    private lateinit var controller: TaskController
    private lateinit var taskAdapter: TaskAdapter

    private lateinit var txtTaskId: TextInputEditText
    private lateinit var txtTaskTitle: TextInputEditText
    private lateinit var txtTaskDescription: TextInputEditText
    private lateinit var txtAssignedTo: TextInputEditText
    private lateinit var txtDueDate: TextInputEditText
    private lateinit var spnTaskStatus: Spinner
    private lateinit var recyclerTasks: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var btnSave: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnClear: Button

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var selectedTaskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        controller = TaskController(this)
        bindViews()
        setupStatusSpinner()
        setupRecyclerView()
        setupListeners()
        loadTasks()
    }

    private fun bindViews() {
        txtTaskId = findViewById(R.id.editTaskId)
        txtTaskTitle = findViewById(R.id.editTaskTitle)
        txtTaskDescription = findViewById(R.id.editTaskDescription)
        txtAssignedTo = findViewById(R.id.editTaskAssignedTo)
        txtDueDate = findViewById(R.id.editTaskDueDate)
        spnTaskStatus = findViewById(R.id.spinnerTaskStatus)
        recyclerTasks = findViewById(R.id.recyclerTasks)
        emptyState = findViewById(R.id.txtEmptyState)
        btnSave = findViewById(R.id.btnSaveTask)
        btnUpdate = findViewById(R.id.btnUpdateTask)
        btnDelete = findViewById(R.id.btnDeleteTask)
        btnClear = findViewById(R.id.btnClearTask)
    }

    private fun setupStatusSpinner() {
        val adapter = android.widget.ArrayAdapter.createFromResource(
            this,
            R.array.task_status_entries,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTaskStatus.adapter = adapter
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskSelected = { task ->
                populateForm(task)
            },
            statusFormatter = { status -> getStatusDisplayName(status) },
            dateFormatter = dateFormatter
        )
        recyclerTasks.apply {
            layoutManager = LinearLayoutManager(this@TaskActivity)
            adapter = taskAdapter
        }
    }

    private fun setupListeners() {
        btnSave.setOnClickListener { saveTask() }
        btnUpdate.setOnClickListener { updateTask() }
        btnDelete.setOnClickListener { deleteTask() }
        btnClear.setOnClickListener { clearForm() }

        txtDueDate.setOnClickListener { showDatePicker() }
        txtDueDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePicker()
            }
        }
    }

    private fun loadTasks() {
        try {
            val tasks = controller.getTasks()
            taskAdapter.submitList(tasks)
            recyclerTasks.isVisible = tasks.isNotEmpty()
            emptyState.isVisible = tasks.isEmpty()
        } catch (e: Exception) {
            showToast(e.message ?: getString(R.string.ErrorMsgGetAll))
        }
    }

    private fun saveTask() {
        val task = buildTaskFromForm() ?: return
        try {
            controller.addTask(task)
            showToast(getString(R.string.msg_task_saved))
            clearForm()
            loadTasks()
        } catch (e: Exception) {
            showToast(e.message ?: getString(R.string.ErrorMsgAdd))
        }
    }

    private fun updateTask() {
        val task = buildTaskFromForm() ?: return
        try {
            controller.updateTask(task)
            showToast(getString(R.string.msg_task_updated))
            clearForm()
            loadTasks()
        } catch (e: Exception) {
            showToast(e.message ?: getString(R.string.ErrorMsgUpdate))
        }
    }

    private fun deleteTask() {
        val taskId = selectedTaskId
        if (taskId.isNullOrEmpty()) {
            showToast(getString(R.string.msg_select_task))
            return
        }
        Util.Util.showDialogCondition(
            this,
            getString(R.string.msg_confirm_delete)
        ) {
            try {
                controller.removeTask(taskId)
                showToast(getString(R.string.msg_task_deleted))
                clearForm()
                loadTasks()
            } catch (e: Exception) {
                showToast(e.message ?: getString(R.string.ErrorMsgRemove))
            }
        }
    }

    private fun populateForm(task: Task) {
        selectedTaskId = task.id
        taskAdapter.selectTask(task.id)
        txtTaskId.setText(task.id)
        txtTaskTitle.setText(task.title)
        txtTaskDescription.setText(task.description)
        txtAssignedTo.setText(task.assignedTo)
        txtDueDate.setText(task.dueDate.format(dateFormatter))
        spnTaskStatus.setSelection(task.status.ordinal)
    }

    private fun clearForm() {
        selectedTaskId = null
        taskAdapter.selectTask(null)
        txtTaskId.setText("")
        txtTaskTitle.setText("")
        txtTaskDescription.setText("")
        txtAssignedTo.setText("")
        txtDueDate.setText("")
        spnTaskStatus.setSelection(0)
        txtTaskId.requestFocus()
    }

    private fun buildTaskFromForm(): Task? {
        val id = txtTaskId.text?.toString()?.trim().orEmpty()
        val title = txtTaskTitle.text?.toString()?.trim().orEmpty()
        val description = txtTaskDescription.text?.toString()?.trim().orEmpty()
        val assignedTo = txtAssignedTo.text?.toString()?.trim().orEmpty()
        val dueDateText = txtDueDate.text?.toString()?.trim().orEmpty()
        val status = TaskStatus.values()[spnTaskStatus.selectedItemPosition]

        if (id.isEmpty() || title.isEmpty() || description.isEmpty() || assignedTo.isEmpty() || dueDateText.isEmpty()) {
            showToast(getString(R.string.error_empty_fields))
            return null
        }

        val dueDate = try {
            LocalDate.parse(dueDateText, dateFormatter)
        } catch (e: DateTimeParseException) {
            showToast(getString(R.string.error_invalid_date))
            return null
        }

        return Task(id, title, description, assignedTo, dueDate, status)
    }

    private fun showDatePicker() {
        val currentDate = txtDueDate.text?.toString()?.takeIf { it.isNotEmpty() }
            ?.let {
                runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull()
            } ?: LocalDate.now()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                txtDueDate.setText(selectedDate.format(dateFormatter))
            },
            currentDate.year,
            currentDate.monthValue - 1,
            currentDate.dayOfMonth
        )
        dialog.show()
    }

    private fun getStatusDisplayName(status: TaskStatus): String = when (status) {
        TaskStatus.PENDING -> getString(R.string.status_pending)
        TaskStatus.IN_PROGRESS -> getString(R.string.status_in_progress)
        TaskStatus.COMPLETED -> getString(R.string.status_completed)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
