package cr.ac.utn.practicaltest.task

import Entity.Task
import Entity.TaskStatus
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import cr.ac.utn.practicaltest.R
import java.time.format.DateTimeFormatter

class TaskAdapter(
    private val onTaskSelected: (Task) -> Unit,
    private val statusFormatter: (TaskStatus) -> String,
    private val dateFormatter: DateTimeFormatter
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val tasks = mutableListOf<Task>()
    private var selectedTaskId: String? = null

    fun submitList(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    fun selectTask(taskId: String?) {
        selectedTaskId = taskId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val isSelected = task.id == selectedTaskId
        holder.bind(task, isSelected, statusFormatter, dateFormatter)
        holder.itemView.setOnClickListener {
            onTaskSelected(task)
        }
    }

    override fun getItemCount(): Int = tasks.size

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardTask)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTaskTitle)
        private val txtAssignedTo: TextView = itemView.findViewById(R.id.txtTaskAssignedTo)
        private val txtDueDate: TextView = itemView.findViewById(R.id.txtTaskDueDate)
        private val txtStatus: TextView = itemView.findViewById(R.id.txtTaskStatus)

        fun bind(
            task: Task,
            selected: Boolean,
            statusFormatter: (TaskStatus) -> String,
            dateFormatter: DateTimeFormatter
        ) {
            txtTitle.text = task.title
            txtAssignedTo.text = itemView.context.getString(
                R.string.item_task_assigned_to_format,
                task.assignedTo
            )
            txtDueDate.text = itemView.context.getString(
                R.string.item_task_due_date_format,
                task.dueDate.format(dateFormatter)
            )
            txtStatus.text = statusFormatter(task.status)

            val statusColorRes = when (task.status) {
                TaskStatus.PENDING -> R.color.task_status_pending
                TaskStatus.IN_PROGRESS -> R.color.task_status_in_progress
                TaskStatus.COMPLETED -> R.color.task_status_completed
            }
            val statusColor = ContextCompat.getColor(itemView.context, statusColorRes)
            val statusBackground = ContextCompat.getDrawable(itemView.context, R.drawable.bg_task_status)?.mutate()
            statusBackground?.setTint(statusColor)
            txtStatus.background = statusBackground
            txtStatus.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))

            val strokeWidth = if (selected) {
                itemView.resources.getDimensionPixelSize(R.dimen.selected_task_stroke)
            } else {
                0
            }
            card.strokeWidth = strokeWidth
            val strokeColor = ContextCompat.getColor(
                itemView.context,
                if (selected) R.color.purple_200 else android.R.color.transparent
            )
            card.setStrokeColor(strokeColor)
        }
    }
}
