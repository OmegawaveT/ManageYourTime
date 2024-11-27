package com.example.manageyourtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.manageyourtime.ui.theme.ManageYourTimeTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() { // 可用 ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManageYourTimeTheme {
                val viewModel: MNTViewModel = viewModel(factory = MNTViewModelFactory(application))
                val pagerState = rememberPagerState(initialPage = viewModel.selectedTab, pageCount = {
                    viewModel.totalpage
                })

                // 页面切换事件监听
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect {page ->
                        // 在这里处理页面切换事件
                        viewModel.selectedTab = page
                    }
                }
                LaunchedEffect(viewModel.selectedTab) {
                    pagerState.animateScrollToPage(viewModel.selectedTab)
                }
                HomePage(pagerState, viewModel)
            }
        }
    }

    @Composable
    private fun HomePage(
        pagerState: PagerState,
        viewModel: MNTViewModel
    ) {
        Scaffold() { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                        ) { page ->
                            when (page) {
                                0 -> weekSchedule(LocalDate.now(), viewModel)
                                1 -> taskList(viewModel) { viewModel.showDialog = true }
                                2 -> completeTaskList(viewModel)
                            }
                        }
                        SmallFloatingActionButton(
                            onClick = {
                                viewModel.resetTimeData()
                                viewModel.showDialog = true//viewmodel.showDialog由单个card修改
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .align(Alignment.BottomEnd) // 对齐到屏幕右下角
                                .padding(horizontal = 32.dp, vertical = 32.dp) // 距离屏幕边缘的内边距
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Small floating action button."
                            )
                        }
                    }
                    bottomBar(viewModel.selectedTab) {
                        viewModel.selectedTab = it
                    }
                }
                if (viewModel.showDialog) {
                    taskDetail(viewModel.DialogData, viewModel) {
                        viewModel.showDialog = it
                    }
                }//taskDetail是顶层的交互逻辑，应该放到最高层的ui中

            }
            if(viewModel.showcompletetaskpage){
                completeTaskList(viewModel)
            }
        }
    }
}
