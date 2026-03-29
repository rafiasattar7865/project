package com.example.campuslife.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslife.R
import com.example.campuslife.model.Task

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val listener: OnTaskClickListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Interface to communicate clicks back to MainActivity
    interface OnTaskClickListener {
        fun onTaskClick(task: Task, position: Int)      // For Update
        fun onTaskLongClick(task: Task, position: Int)  // For Delete
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtTaskTitle)
        val description: TextView = itemView.findViewById(R.id.txtTaskDesc)
        val categoryBadge: TextView = itemView.findViewById(R.id.txtCategoryBadge)
        val dateTime: TextView = itemView.findViewById(R.id.txtDateTime)
        val priorityStar: TextView = itemView.findViewById(R.id.txtPriorityStar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val t = tasks[position]
        holder.title.text = t.title
        holder.description.text = t.description
        holder.categoryBadge.text = t.category
        holder.dateTime.text = "${t.date} | ${t.time}"

        holder.priorityStar.visibility = if (t.isImportant) View.VISIBLE else View.GONE
        holder.description.visibility = if (t.description.isNullOrEmpty()) View.GONE else View.VISIBLE

        // Click to Edit
        holder.itemView.setOnClickListener { listener.onTaskClick(t, position) }

        // Long Click to Delete
        holder.itemView.setOnLongClickListener {
            listener.onTaskLongClick(t, position)
            true
        }
    }

    fun addTask(task: Task) {
        tasks.add(0, task)
        notifyItemInserted(0)
    }

    fun updateTask(task: Task, position: Int) {
        tasks[position] = task
        notifyItemChanged(position)
    }

    fun removeTask(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }
}