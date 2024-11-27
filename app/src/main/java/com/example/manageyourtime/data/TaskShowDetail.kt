package com.example.manageyourtime.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.manageyourtime.utils.toDateString
import com.example.manageyourtime.utils.toTimeString


class TaskShowDetail(
    id: Int = -1,
    name: String = "",
    startdate: String = "",
    starttime: String = "",
    endtime: String = "",
    description: String = "",
    iscompleted: Boolean = false,
    isdeleted: Boolean = false
) {
    var id by mutableStateOf(id)
    var name by mutableStateOf(name)
    var date by mutableStateOf(startdate)
    var starttime by mutableStateOf(starttime)
    var endtime by mutableStateOf(endtime)
    var description by mutableStateOf(description)
    var iscompleted by mutableStateOf(iscompleted)
    var isdeleted by mutableStateOf(isdeleted)

    // 使用 ScheduledTask 初始化的构造函数
    constructor(scheduledTask: ScheduledTask) : this() {
        this.id = scheduledTask.task.id
        this.name = scheduledTask.task.name
        this.description = scheduledTask.task.note
        this.date = scheduledTask.start.toDateString()
        this.starttime = scheduledTask.start.toTimeString()
        this.endtime = scheduledTask.end.toTimeString()
        if(scheduledTask.start == -1L || scheduledTask.start == -1L){
            this.date = ""
            this.starttime = ""
            this.endtime = ""
        }
    }
}