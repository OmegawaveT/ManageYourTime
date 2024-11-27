package com.example.manageyourtime

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable

@Composable
fun bottomBar(selected: Int, onSelectedChanged: (Int) -> Unit) {//lambda表达式，表示传入了一个修改了什么的参数，避免访问viewmodel解除耦合
    Row {
        tabItem(
            iconId = R.drawable.baseline_calendar_month_24,
            title = "日程",
            tint = if (selected == 0) Color.Blue else Color.Black,
            Modifier.weight(1f).clickable{
                onSelectedChanged(0)
            }
        )
        tabItem(
            iconId = R.drawable.baseline_checklist_24,
            title = "列表",
            tint = if (selected == 1) Color.Blue else Color.Black,
            Modifier.weight(1f).clickable{
                onSelectedChanged(1)
            }
        )
        tabItem(
            iconId = R.drawable.baseline_check_circle_outline_24,
            title = "记录",
            tint = if (selected == 2) Color.Blue else Color.Black,
            Modifier.weight(1f).clickable{
                onSelectedChanged(2)
            }
        )
    }
}

@Composable
fun tabItem(@DrawableRes iconId: Int, title: String, tint: Color, modifier: Modifier = Modifier) {
    Column (
        modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Icon(
            painter = painterResource(iconId),
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(title, fontSize = 11.sp, color=tint)
    }
}




