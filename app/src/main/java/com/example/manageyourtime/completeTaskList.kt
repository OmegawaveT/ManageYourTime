package com.example.manageyourtime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun completeTaskList(viewmodel: MNTViewModel) {

    var completedtasks = viewmodel.completedtask

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatterdatetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(completedtasks) { task ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp)  // 添加左右 2dp 的 padding
                        .height(100.dp)  // 固定高度
                        .border(
                            BorderStroke(3.dp, Color.Red),
                            shape = RoundedCornerShape(8.dp)
                        )  // 设置边框颜色为红色，宽度为2dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 4.dp)  // 添加左右 2dp 的 padding
                    ) {
                        Row {
                            Text(task.name, fontSize = 20.sp, modifier = Modifier.weight(1f))
                            Text(
                                Instant.ofEpochMilli(task.created).atZone(ZoneId.systemDefault())
                                    .toLocalDateTime().format(formatter),
                                Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                        }
                        Text(task.note, modifier = Modifier.weight(1f))
                        if ((task.starttime != -1L) and (task.endtime != -1L)) {//时间提示框
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "开始时间：${
                                        Instant.ofEpochMilli(task.starttime)
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
                                        Instant.ofEpochMilli(task.endtime)
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
            }
        }
    }
}
