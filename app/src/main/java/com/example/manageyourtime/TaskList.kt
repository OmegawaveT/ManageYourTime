package com.example.manageyourtime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier


import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight
import com.example.manageyourtime.data.ScheduledTask

@Composable
fun taskList(viewmodel: MNTViewModel, onClickCard: (Boolean) -> Unit) {
    var alltasks = viewmodel.tasks
    var scheduled = viewmodel.scheduled
    var unscheduled = alltasks.filter { task ->
        scheduled.none{ scheduledTask -> scheduledTask.task.id == task.id }
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatterdatetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        item{
            Row (modifier = Modifier.padding(vertical = 4.dp)) {
                Box(contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.fillMaxWidth()
                        .background(Color(0xFFDDDDDD))
                     ){
                    Text("已安排的任务", fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp))
                }
            }
        }
        items(scheduled){ task->
            Surface(
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)  // 添加左右 2dp 的 padding
                    .height(100.dp)  // 固定高度
                    .border(BorderStroke(3.dp, Color(0xFFFF99CC)), shape = RoundedCornerShape(8.dp))  // 设置边框颜色为红色，宽度为2dp
                    .clickable{
                        viewmodel.resetTimeData(task)
                        onClickCard(true)
                    }
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp
                        ) // 添加左右 2dp 的 padding
                ){
                    Row{
                        Text(task.task.name, fontSize = 20.sp, modifier = Modifier.weight(1f))
                        Text(Instant.ofEpochMilli(task.task.created).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter), Modifier.align(Alignment.CenterVertically))
                    }
                    Text(task.task.note, modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "开始时间：${
                                Instant.ofEpochMilli(task.start)
                                    .atZone(ZoneId.systemDefault()).toLocalDateTime()
                                    .format(formatterdatetime)
                            }",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                        Text(
                            "结束时间：${
                                Instant.ofEpochMilli(task.end)
                                    .atZone(ZoneId.systemDefault()).toLocalDateTime()
                                    .format(formatterdatetime)
                            }",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                        )
                    }
                }
            }
        }
        item{
            Row (modifier = Modifier.padding(vertical = 4.dp)) {
                Box(contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.fillMaxWidth()
                        .background(Color(0xFFDDDDDD))
                ){
                    Text("未安排的任务", fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp))
                }
            }
        }
        items(unscheduled){ task->
            Surface(
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)  // 添加左右 2dp 的 padding
                    .height(100.dp)  // 固定高度
                    .border(BorderStroke(3.dp, Color(0xFFFF77FF)), shape = RoundedCornerShape(8.dp))  // 设置边框颜色为红色，宽度为2dp
                    .clickable{
                        viewmodel.resetTimeData(ScheduledTask(task))//需要用已有的task新建一个Scheduled
                        onClickCard(true)
                    }
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp)  // 添加左右 2dp 的 padding
                ){
                    Row{
                        Text(task.name, fontSize = 20.sp, modifier = Modifier.weight(1f))
                        Text(Instant.ofEpochMilli(task.created).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter), Modifier.align(Alignment.CenterVertically))
                    }
                    Text(task.note)
                    /*
                    Row {
                        val ddl: String
                        if(task.deadline == (-1).toLong()) ddl="暂无截止日期"
                        else ddl = Instant.ofEpochMilli(task.created).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter)
                        Text(ddl)
                    }
                     */
                }
            }
        }
    }
}
