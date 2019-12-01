package es.iessaladillo.pedrojoya.pr04.ui.main

import android.content.ActivityNotFoundException
import android.media.Image
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.base.observeEvent
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.hideKeyboard
import es.iessaladillo.pedrojoya.pr04.utils.invisibleUnless
import es.iessaladillo.pedrojoya.pr04.utils.setOnSwipeListener
import kotlinx.android.synthetic.main.tasks_activity.*
import es.iessaladillo.pedrojoya.pr04.utils.hideKeyboard


class TasksActivity : AppCompatActivity(), TasksActivityAdapter.OnItemClick {
    private var mnuFilter: MenuItem? = null

    private lateinit var lblEmptyView: TextView
    private lateinit var txtConcept: EditText
    private lateinit var imgAddTask: ImageView

    private val viewModel: TasksActivityViewModel by viewModels{
        TasksActivityViewModelFactory(LocalRepository, application)
    }
    val listAdapter: TasksActivityAdapter = TasksActivityAdapter().apply {
        setOnItemClick(this@TasksActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_activity)
        setupViews()
        observeTasks()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        mnuFilter = menu.findItem(R.id.mnuFilter)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupViews() {
        setupRecyclerView()
        lblEmptyView = findViewById(R.id.lblEmptyView)
        txtConcept = findViewById(R.id.txtConcept)
        imgAddTask = findViewById(R.id.imgAddTask)


        //EVENTS
        imgAddTask.setOnClickListener({ viewModel.addTask(txtConcept.text.toString())
        imgAddTask.hideKeyboard()})
    }

    private fun setupRecyclerView() {
        lstTasks.run{
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = listAdapter

            setOnSwipeListener { viewHolder, direction ->
                val task = listAdapter.tasks[viewHolder.adapterPosition]
                deleteTask(task)
            }
        }
    }

    fun deleteTask(task: Task){
        viewModel.deleteTask(task)
        Snackbar.make(lstTasks, getString(R.string.tasks_task_deleted, task.concept), Snackbar.LENGTH_LONG)
            .setAction(R.string.tasks_recreate){viewModel.insertTask(task)}
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuShare -> viewModel.shareTasks()
            R.id.mnuDelete -> viewModel.deleteTasks()
            R.id.mnuComplete -> viewModel.markTasksAsCompleted()
            R.id.mnuPending -> viewModel.markTasksAsPending()
            R.id.mnuFilterAll -> viewModel.filterAll()
            R.id.mnuFilterPending -> viewModel.filterPending()
            R.id.mnuFilterCompleted -> viewModel.filterCompleted()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun checkMenuItem(@MenuRes menuItemId: Int) {
        lstTasks.post {
            val item = mnuFilter?.subMenu?.findItem(menuItemId)
            item?.let { menuItem ->
                menuItem.isChecked = true
            }
        }
    }

    private fun showTasks(tasks: List<Task>) {
        lstTasks.post {
            listAdapter.submitList(tasks)
            lblEmptyView.invisibleUnless(tasks.isEmpty())
        }
    }

    private fun observeTasks() {
        viewModel.taskList.observe(this){
            updateList(it)
        }
    }

    private fun observeMenu(){
        viewModel.currentFilterMenuItemId.observe(this){

        }
    }

    private fun updateList(newList: List<Task>){
        listAdapter.submitList(newList)
        lblEmptyView.visibility = if(newList.isEmpty()) View.VISIBLE else View.INVISIBLE
    }

    override fun onTaskClick(position: Int) {
        val clickedTask = listAdapter.getItem(position)

        viewModel.updateTaskCompletedState(clickedTask, clickedTask.completed)
    }

}

