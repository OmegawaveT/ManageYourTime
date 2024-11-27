package com.example.manageyourtime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manageyourtime.data.ScheduledTask
import com.example.manageyourtime.data.Task
import com.example.manageyourtime.utils.getWeekDates
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

//

@Composable
fun weekSchedule(currentdate: LocalDate, viewmodel: MNTViewModel) {
    //给task找到对应的栏位
    var weekdates = getWeekDates(currentdate)
    BoxWithConstraints(Modifier.fillMaxSize()) { // 父布局，提供约束
        val parentHeight = maxHeight*0.8f // 获取父布局的高度
        LazyVerticalGrid(
            columns = GridCells.Fixed(7), // 设置为 7 列
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(7) { index ->
                dateBox(index, weekdates, currentdate)
            }
            items(7) { index ->
                Card(//背景
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(230,230,230)), // 设置背景颜色为灰色
                    modifier = Modifier
                        .fillMaxWidth() // 每列占满宽度
                        .height(parentHeight) // 设置为父布局高度的 75%
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ){
                        showTaskCards(parentHeight, index, weekdates[index], viewmodel)
                    }
                }
            }
        }
    }
}

//将task按天分开
@Composable
private fun showTaskCards(parentHeight: Dp, index: Int, weekday:LocalDate, viewmodel: MNTViewModel) {
    val startOfDay = weekday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
    val endOfDay = weekday.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

    viewmodel.scheduled
    .filter { task -> task.start >= startOfDay && task.start <= endOfDay }
    .sortedBy { it.start } // 按开始时间排序
    .forEach { task ->
        val startPercent = (task.start - startOfDay).toFloat() / (endOfDay - startOfDay).toFloat()
        val endPercent = (task.end - startOfDay).toFloat() / (endOfDay - startOfDay).toFloat()
        // 添加任务card
        if (endPercent <= 1){
            TaskCard(parentHeight, index, startPercent, endPercent, task.task){
                viewmodel.showDialog = it//viewmodel.showDialog由单个card修改
                viewmodel.resetTimeData(task)
            }
        }
        else{//处理长期任务情况
            TaskCard(parentHeight, index, startPercent, 1.0f, task.task){
                viewmodel.showDialog = it//viewmodel.showDialog由单个card修改
                viewmodel.resetTimeData(task)
            }

        }
    }
}
//单个card，百分比start到end
@Composable
private fun TaskCard(parentHeight: Dp, index: Int, start: Float, end: Float, task: Task, onClickCard: (Boolean) -> Unit) {
    Card(//内层card
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth() // 每列占满宽度
            .offset(y = parentHeight * (start))
            .height(parentHeight * (end-start)) // 高度为父布局的 (end - start) 部分
            .border(BorderStroke(2.dp, Color(0xFFBB9988)), shape = RoundedCornerShape(8.dp))  // 设置边框颜色为红色，宽度为2dp
            .padding(2.dp)
            .clickable{
                onClickCard(true)
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column()
            {
                Text(
                    text = task.name,
                    fontSize = 15.sp,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    maxLines = 3, // 限制为一行
                    overflow = TextOverflow.Ellipsis // 超出部分用省略号表示
                )
            }

        }
    }
}
//绘制日期tag
@Composable
fun dateBox(index: Int, weekdates: List<LocalDate>, currentdate: LocalDate) {
    var weeklist = listOf("MON","TUE","WED","THU","FRI","SAT","SUN")
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (currentdate.dayOfMonth == weekdates[index].dayOfMonth) Color(220,220,240) else Color(220,220,220)) , // 设置背景颜色为灰色
        modifier = Modifier
            .fillMaxWidth() // 每列占满宽度

    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = weeklist[index],
                fontSize = 18.sp,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = weekdates[index].dayOfMonth.toString(),
                fontSize = 32.sp,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    val viewModel: MNTViewModel = viewModel()
    weekSchedule(LocalDate.now(), viewModel)
}