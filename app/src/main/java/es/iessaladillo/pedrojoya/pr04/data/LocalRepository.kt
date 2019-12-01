package es.iessaladillo.pedrojoya.pr04.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalRepository : Repository{

    var counter: Long = 1;

    var taskList: MutableList<Task> = mutableListOf()
    private val tasksLiveData: MutableLiveData<List<Task>> =  MutableLiveData(taskList)


    override fun queryAllTasks(): LiveData<List<Task>> {
        tasksLiveData.value = ArrayList<Task>(taskList.sortedByDescending { it.id })
        return tasksLiveData
    }

    override fun queryCompletedTasks(): LiveData<List<Task>> {
        var completedTasksList: MutableList<Task> = mutableListOf()

        for(task in taskList){
            if(task.completed){
                completedTasksList.add(task)
            }
        }
        tasksLiveData.value = ArrayList<Task>(completedTasksList).sortedByDescending { it.id }
        return tasksLiveData
    }

    override fun queryPendingTasks(): LiveData<List<Task>> {
        var pendingTasksList: MutableList<Task> = mutableListOf()

        for(task in taskList){
            if(!task.completed){
                pendingTasksList.add(task)
            }
        }
        tasksLiveData.value = ArrayList<Task>(pendingTasksList).sortedByDescending { it.id }
        return tasksLiveData
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addTask(concept: String) {
        val creationDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss")
        val formattedDate: String = creationDate.format(formatter)

        var newTask: Task = Task(counter, concept, formattedDate , false, "")
        insertTask(newTask)
    }

    override fun insertTask(task: Task) {
        taskList.add(task)
        tasksLiveData.value = ArrayList<Task>(taskList.sortedByDescending { it.id })
        counter++
    }

    override fun deleteTask(taskId: Long) {
        var taskToDelete: Task? = null

        for(task in taskList){
            if(task.id == taskId){
                taskToDelete = task
            }
        }
        taskList.remove(taskToDelete)
        tasksLiveData.value = ArrayList<Task>(taskList.sortedByDescending { it.id })
    }

    override fun deleteTasks(taskIdList: List<Long>) {
        for(taskId in taskIdList){
            deleteTask(taskId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTaskAsCompleted(taskId: Long) {
        val completedDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss")
        val formattedDate: String = completedDate.format(formatter)

        for(task in taskList){
            if(task.id == taskId){
                task.completed = true
                task.completedAt = formattedDate
            }
        }
        tasksLiveData.value = ArrayList<Task>(taskList.sortedByDescending { it.id })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTasksAsCompleted(taskIdList: List<Long>) {
        val completedDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss")
        val formattedDate: String = completedDate.format(formatter)

        for(task in taskList){
            for(taskId in taskIdList){
                if(task.id == taskId){
                    task.completed = true
                    task.completedAt = formattedDate
                }
            }
        }

        tasksLiveData.value = ArrayList<Task>(taskList.sortedByDescending { it.id })
    }

    override fun markTaskAsPending(taskId: Long) {
        for(task in taskList){
            if(task.id == taskId){
                task.completed = false
            }
        }
        tasksLiveData.value = ArrayList<Task>(taskList.sortedByDescending { it.id })
    }

    override fun markTasksAsPending(taskIdList: List<Long>) {
        for(task in taskList){
            for(taskId in taskIdList){
                if(task.id == taskId){
                    task.completed = false
                }
            }
        }

        tasksLiveData.value = ArrayList<Task>(taskList.sortedByDescending { it.id })
    }

}



