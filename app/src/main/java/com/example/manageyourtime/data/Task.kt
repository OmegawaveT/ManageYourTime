package com.example.manageyourtime.data

data class Task (
    var id: Int = -1,
    var name: String = "",
    var importance: Int = 1,
    var note: String = "",
    var created: Long = System.currentTimeMillis(),
    var last_updated: Long = System.currentTimeMillis(),
    var deadline: Long = -1,//-1就是没有
    var complete: Boolean = false,
    var isdeleted: Boolean = false
){

}
data class ScheduledTask(
    var task: Task,
    var start: Long = -1,
    var end: Long = -1,
){

}