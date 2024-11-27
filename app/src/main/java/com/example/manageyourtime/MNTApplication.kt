package com.example.manageyourtime

import android.app.Application
import com.example.manageyourtime.database.TaskDataRepository
import com.example.manageyourtime.database.TaskDatabase

class MNTApplication : Application() {

    // 数据库实例
    lateinit var database: TaskDatabase

    // Repository 实例
    lateinit var repository: TaskDataRepository

    override fun onCreate() {
        super.onCreate()

        // 初始化数据库
        database = TaskDatabase.getDatabase(this)

        // 初始化 Repository
        repository = TaskDataRepository(database.taskDao())

    }
}