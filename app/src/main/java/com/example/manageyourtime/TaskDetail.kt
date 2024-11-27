package com.example.manageyourtime

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manageyourtime.data.TaskShowDetail
import com.example.manageyourtime.utils.toDateString
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.lifecycle.viewModelScope
import com.example.manageyourtime.utils.convertDateTimeToTimestamp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun taskDetail(taskdetail: TaskShowDetail,  viewmodel: MNTViewModel, onClickCard: (Boolean) -> Unit) {

    AlertDialog(
        onDismissRequest = {
            // 点击对话框外部或返回键关闭对话框
            onClickCard(false)
        },
        confirmButton = {

            Button(onClick = {
                if(havetime(taskdetail)){
                    if(!nameempty(taskdetail) and !timeerror(taskdetail)) {
                        viewmodel.updateScheduledTask()
                        onClickCard(false)
                    }
                }
                else{
                    if(!nameempty(taskdetail)){
                        viewmodel.updateScheduledTask()
                        onClickCard(false)
                    }
                }
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(onClick = { onClickCard(false) }) {
                Text("取消")
            }
        },
        title = {
            Text(text = "日程信息")
        },
        text = {
            Column() {
                TextField(
                    value = taskdetail.name,
                    onValueChange = { taskdetail.name = it },
                    label = { Text("任务") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "时间安排")
                TextField(
                    value = taskdetail.date,
                    onValueChange = {},
                    label = { Text("日期") },
                    trailingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = "Select date")
                    },
                    modifier = Modifier.pointerInput(taskdetail.date) {
                        awaitEachGesture {
                            // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                            // in the Initial pass to observe events before the text field consumes them
                            // in the Main pass.
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                viewmodel.showDatePicker = true
                            }
                        }
                    }
                )
                if (viewmodel.showDatePicker) {
                    DatePickerModal(
                        onDateSelected = { viewmodel.DialogData.date = it?.toDateString() ?: "" },
                        onDismiss = { viewmodel.showDatePicker = false }
                    )
                }

                Row() {
                    TextField(
                        value = taskdetail.starttime,
                        onValueChange = {},
                        label = { Text("开始时间") },
                        trailingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = "Select date")
                        },
                        modifier = Modifier.weight(1f).pointerInput(taskdetail.starttime) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    viewmodel.selectedtime = false
                                    viewmodel.showTimePicker = true
                                }
                            }
                        }
                    )
                    TextField(
                        value = taskdetail.endtime,
                        onValueChange = {},
                        label = { Text("结束时间") },
                        trailingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = "Select date")
                        },
                        modifier = Modifier.weight(1f).pointerInput(taskdetail.starttime) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    viewmodel.selectedtime = true
                                    viewmodel.showTimePicker = true
                                }
                            }
                        }
                    )
                }
                if(viewmodel.showTimePicker){
                    if(!viewmodel.selectedtime){
                        DialWithDialogExample(
                            onConfirm = {
                                taskdetail.starttime = "${it.hour}:${it.minute}"
                                viewmodel.showTimePicker = false },
                            onDismiss = {viewmodel.showTimePicker = false},
                        )
                    }
                    else{
                        DialWithDialogExample(
                            onConfirm = {
                                taskdetail.endtime = "${it.hour}:${it.minute}"
                                viewmodel.showTimePicker = false },
                            onDismiss = {viewmodel.showTimePicker = false},
                        )
                    }
                }
                Button(modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = {
                        taskdetail.date = ""
                        taskdetail.starttime = ""
                        taskdetail.endtime = ""
                    }
                ) {
                    Text("重置", fontSize = 16.sp ,modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                Row(verticalAlignment = Alignment.CenterVertically,){
                    Row (verticalAlignment = Alignment.CenterVertically,modifier = Modifier.weight(1f)){
                        Text("完成", fontSize = 16.sp,textAlign = TextAlign.Center)
                        Checkbox(
                            checked = viewmodel.DialogData.iscompleted,
                            onCheckedChange = { viewmodel.DialogData.iscompleted = it }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.weight(1f)) {
                        Text("删除", fontSize = 16.sp,textAlign = TextAlign.Center)
                        Checkbox(
                            checked = viewmodel.DialogData.isdeleted,
                            onCheckedChange = { viewmodel.DialogData.isdeleted = it }
                        )
                    }
                }
                TextField(
                    value = taskdetail.description,
                    onValueChange = { taskdetail.description = it },
                    label = { Text("描述") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                )
                if(nameempty(taskdetail)){Text("! 任务名不能为空" )}
                if(havetime(taskdetail)){
                    if(timeerror(taskdetail)){Text("! 结束时间不能早于开始时间") }
                }
            }
        }
    )
}


private fun timeerror(
    taskdetail: TaskShowDetail,
): Boolean {
    if((taskdetail.starttime == "") and (taskdetail.endtime == "") and (taskdetail.date == "")) return false
    var start = convertDateTimeToTimestamp(taskdetail.date + " " + taskdetail.starttime)
    var end = convertDateTimeToTimestamp(taskdetail.date + " " + taskdetail.endtime)
    var timecheck = start >= end
    return timecheck
}


private fun nameempty(
    taskdetail: TaskShowDetail
): Boolean {
    var namefill = (taskdetail.name == "")
    return namefill
}


private fun havetime(taskdetail: TaskShowDetail): Boolean {
    var havetime: Boolean =
        (taskdetail.date != "" )&& (taskdetail.starttime != "" )&& (taskdetail.endtime != "")
    return havetime
}





