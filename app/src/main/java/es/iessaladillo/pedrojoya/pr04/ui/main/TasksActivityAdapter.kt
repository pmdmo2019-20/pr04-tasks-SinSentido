package es.iessaladillo.pedrojoya.pr04.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tasks_activity_item.*
import es.iessaladillo.pedrojoya.pr04.utils.strikeThrough

class TasksActivityAdapter : RecyclerView.Adapter<TasksActivityAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    var tasks: List<Task> = emptyList()
    private var onItemClick: TasksActivityAdapter.OnItemClick? = null

    fun setOnItemClick(listener: TasksActivityAdapter.OnItemClick){
        onItemClick = listener
    }

    override fun getItemId(position: Int): Long = tasks[position].id

    override fun getItemCount(): Int = tasks.size

    fun getItem(position: Int): Task = tasks[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val itemView = layoutInflater.inflate(R.layout.tasks_activity_item, parent, false)

        return ViewHolder(itemView)
    }

    fun submitList(newTasks: List<Task>){
        tasks = newTasks
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val lblConcept: TextView = itemView.findViewById(R.id.lblConcept)
        private val lblCompleted: TextView = itemView.findViewById(R.id.lblCompleted)
        private val chkCompleted: CheckBox = itemView.findViewById(R.id.chkCompleted)
        private val viewBar: View = itemView.findViewById(R.id.viewBar)

        init{
            itemView.setOnClickListener(){ onItemClick?.onTaskClick(adapterPosition) }
            chkCompleted.setOnClickListener(){onItemClick?.onTaskClick(adapterPosition)}
        }

        fun bind(task: Task) {
            task.run{
                lblConcept.text = concept
                if (completed) {
                    lblConcept.strikeThrough(true)
                    lblCompleted.setText(itemView.resources.getString(R.string.tasks_item_completedAt, task.completedAt))
                    chkCompleted.isChecked = true
                    viewBar.setBackgroundResource(R.color.colorCompletedTask)
                } else {
                    lblConcept.strikeThrough(false)
                    lblCompleted.setText(itemView.resources.getString(R.string.tasks_item_createdAt, task.createdAt))
                    chkCompleted.isChecked = false
                    viewBar.setBackgroundResource(R.color.colorPendingTask)
                }
            }
        }

    }

    interface OnItemClick{
        fun onTaskClick(position: Int)
    }
}



