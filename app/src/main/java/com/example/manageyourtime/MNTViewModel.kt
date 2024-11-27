package com.example.manageyourtime

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.manageyourtime.data.ScheduledTask
import com.example.manageyourtime.data.Task
import com.example.manageyourtime.data.TaskShowDetail
import com.example.manageyourtime.database.TaskData
import com.example.manageyourtime.database.TaskDataRepository
import com.example.manageyourtime.database.TaskDatabase
import com.example.manageyourtime.utils.getTimestampFromDateTime
import com.example.manageyourtime.utils.convertDateTimeToTimestamp
import java.time.Instant
import com.example.manageyourtime.utils.toDateString
import com.example.manageyourtime.utils.toTimeString
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId



class MNTViewModel(application: Application) : AndroidViewModel(application)  {
    private val taskRepository = TaskDataRepository(TaskDatabase.getDatabase(application).taskDao())
    var selectedTab by mutableStateOf(0)//刷新监听器
    var totalpage = 3

    var tasks = mutableStateListOf<Task>()

    var scheduled = mutableStateListOf<ScheduledTask>()

    var completedtask = mutableStateListOf<TaskData>()
    var showcompletetaskpage by mutableStateOf(false)

    var showDialog by mutableStateOf(false)//刷新监听器
    var DialogData by mutableStateOf(TaskShowDetail())


    var showDatePicker by mutableStateOf(false)
    var DatePickerValue by mutableStateOf<Long?>(null)
    var selectedtime by mutableStateOf(false)//false开始，true结束
    var showTimePicker by mutableStateOf(false)
    var TimePickerValue by mutableStateOf<Long?>(null)

    init {
        // 构造函数，将database中的数据转移到tasks和scheduled中
        //用协程分到子进程中处理
        viewModelScope.launch {
            var alltaskdata: List<TaskData>? = taskRepository.getTaskByStatus(false, false)
            if (!alltaskdata.isNullOrEmpty()) {
                //遍历每个taskdata构造tasks，根据是否有时间设置构造scheduled
                alltaskdata.forEach { taskData ->
                    // 根据任务数据构造 Task
                    val task = Task(
                        id = taskData.id,
                        name = taskData.name,
                        importance = taskData.importance,
                        note = taskData.note,
                        created = taskData.created,
                        last_updated = taskData.last_updated,
                        deadline = taskData.deadline,
                        complete = taskData.complete
                    )
                    tasks.add(task)

                    // 检查是否有时间安排
                    if ((taskData.starttime != -1L) and (taskData.endtime != -1L) and (taskData.starttime < taskData.endtime)) {
                        // 构造 ScheduledTask（假设有一个 ScheduledTask 数据类）
                        scheduled.add(
                            ScheduledTask(
                                task = task,
                                start = taskData.starttime,
                                end = taskData.endtime
                            )
                        )
                    }
                }
            }
        }
        getCompletedTask()
    }

    fun resetTimeData(TaskDetail: ScheduledTask? = null){
        DialogData = if (TaskDetail == null) {
                TaskShowDetail()
            } else {
                TaskShowDetail(TaskDetail)
            }
    }

    fun updateScheduledTask(){
        /*执行判断，分情况：
        1 id是-1 则必定为新增
            1-时间有安排 ->只在task中新增
            2-时间无安排 ->在task和scheduled中同时新增
        2 id不是-1 则分情况
            1>在scheduled中存在
                1-时间有安排 ->更新
                2-时间无安排 ->在scheduled中移除
            2>在schedule的中不存在
                1-时间有安排 ->在scheduled中新增
                2-时间无安排 ->更新

         */
        val havetime: Boolean = DialogData.date != "" && DialogData.starttime!="" && DialogData.endtime !=""
        //还需要处理complete和isdeleted相关的逻辑
        //当complete和isdeleted同时为false时显示，否则隐藏
        //只需要在最后添加filter即可
        if (DialogData.id == -1){//id是-1 则必定为新增
            //需要处理id新增问题，先插入数据库获取最新id再将id覆盖到task中，这里先使用最大值处理
            if(havetime){
                viewModelScope.launch{
                    taskRepository.addTask(
                        TaskData(
                            importance = 1,
                            name = DialogData.name,
                            note = DialogData.description,
                            deadline = -1,
                            starttime = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.starttime),
                            endtime = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.endtime)
                        )
                    )
                    scheduled.add(
                        ScheduledTask(
                            Task(
                                id = taskRepository.getLastId().toInt(),//todo
                                importance = 1,
                                name = DialogData.name,
                                note = DialogData.description,
                                created = Instant.now().toEpochMilli(),
                                last_updated = Instant.now().toEpochMilli(),
                                deadline = -1,
                                complete = false
                            ),
                            start = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.starttime),
                            end = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.endtime)
                        )
                    )

                    tasks.add(
                        Task(
                            id = taskRepository.getLastId().toInt(),//todo
                            importance = 1,
                            name = DialogData.name,
                            note = DialogData.description,
                            created = Instant.now().toEpochMilli(),
                            last_updated = Instant.now().toEpochMilli(),
                            deadline = -1,
                            complete = false
                        )
                    )
                }
            }
            else{
                viewModelScope.launch{
                    taskRepository.addTask(
                        TaskData(
                            importance = 1,
                            name = DialogData.name,
                            note = DialogData.description,
                            deadline = -1,
                        )
                    )
                    tasks.add(
                        Task(
                            id = taskRepository.getLastId().toInt(),//todo
                            importance = 1,
                            name = DialogData.name,
                            note = DialogData.description,
                            created = Instant.now().toEpochMilli(),
                            last_updated = Instant.now().toEpochMilli(),
                            deadline = -1,
                            complete = false
                        )
                    )
                }


            }

        }
        else{//id不是-1,使用update
            val sindex = scheduled.indexOfFirst { it.task.id == DialogData.id }
            val tindex = tasks.indexOfFirst { it.id == DialogData.id }
            if (sindex != -1) {//在scheduled中存在
                if(havetime){//时间有安排
                    //更新scheduled,使用原本的scheduled数据填写
                    viewModelScope.launch{
                        taskRepository.updateTask(
                            id = DialogData.id,
                            name = DialogData.name,
                            note = DialogData.description,
                            starttime = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.starttime),
                            endtime = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.endtime),
                            iscomplete = DialogData.iscompleted,
                            isdeleted = DialogData.isdeleted,
                        )
                    }
                    scheduled[sindex] = scheduled[sindex].copy(
                        task = scheduled[sindex].task.copy(
                            id = scheduled[sindex].task.id,
                            importance = scheduled[sindex].task.importance,
                            name = DialogData.name,
                            note = DialogData.description,
                            created = scheduled[sindex].task.created,
                            last_updated = Instant.now().toEpochMilli(),
                            deadline = scheduled[sindex].task.deadline,
                            complete = DialogData.iscompleted,
                            isdeleted = DialogData.isdeleted,
                        ),
                        start = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.starttime),
                        end = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.endtime)
                    )
                    //更新task
                    updateTask(tindex)
                }
                else{//时间无安排
                    //移除scheduled
                    viewModelScope.launch{
                        taskRepository.updateTask(
                            id = DialogData.id,
                            name = DialogData.name,
                            note = DialogData.description,
                            starttime = -1L,
                            endtime = -1L
                        )
                    }
                    scheduled.removeAt(sindex)
                    //更新task
                    updateTask(tindex)
                }
            }
            else{//在scheduled中不存在
                val tindex = tasks.indexOfFirst { it.id == DialogData.id }
                if(havetime){//有时间安排
                    //新建schedule，考虑到修改过所以用dialogdata里的创建
                    viewModelScope.launch{
                        taskRepository.updateTask(
                            id = DialogData.id,
                            name = DialogData.name,
                            note = DialogData.description,
                            starttime = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.starttime),
                            endtime = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.endtime),
                            iscomplete = DialogData.iscompleted,
                            isdeleted = DialogData.isdeleted,
                        )
                    }
                    scheduled.add(
                        ScheduledTask(
                            Task(
                                id = tasks[tindex].id,
                                importance = tasks[tindex].importance,
                                name = DialogData.name,
                                note = DialogData.description,
                                created = tasks[tindex].created,
                                last_updated = Instant.now().toEpochMilli(),
                                deadline = tasks[tindex].deadline,
                                complete = DialogData.iscompleted,
                                isdeleted = DialogData.isdeleted,
                            ),
                            start = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.starttime),
                            end = convertDateTimeToTimestamp(DialogData.date + " " + DialogData.endtime)
                        )
                    )
                    //更新task
                    updateTask(tindex)
                }
                else{
                    //无需管理schedule
                    //更新task
                    updateTask(tindex)
                    viewModelScope.launch{
                        taskRepository.updateTask(
                            id = DialogData.id,
                            name = DialogData.name,
                            note = DialogData.description,
                            starttime = -1L,
                            endtime = -1L,
                            iscomplete = DialogData.iscompleted,
                            isdeleted = DialogData.isdeleted,
                        )
                    }
                }
            }
        }
        tasks.removeIf { task -> task.complete || task.isdeleted }
        scheduled.removeIf { stask -> stask.task.complete || stask.task.isdeleted }
        getCompletedTask()
    }

    fun getCompletedTask(){
        viewModelScope.launch {
            completedtask = taskRepository.getTaskByStatus(true, false)?.toMutableStateList() ?: mutableStateListOf<TaskData>()
        }
    }

    private fun updateTask(tindex: Int) {
        tasks[tindex] = tasks[tindex].copy(
            id = tasks[tindex].id,
            importance = tasks[tindex].importance,
            name = DialogData.name,
            note = DialogData.description,
            created = tasks[tindex].created,
            last_updated = Instant.now().toEpochMilli(),
            deadline = tasks[tindex].deadline,
            complete = DialogData.iscompleted,
            isdeleted = DialogData.isdeleted
        )
    }
}

class MNTViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MNTViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MNTViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

