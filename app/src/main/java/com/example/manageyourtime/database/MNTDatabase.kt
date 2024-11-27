package com.example.manageyourtime.database

import android.app.Application
import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.manageyourtime.taskDetail


@Entity(tableName = "TaskDataTable")
data class TaskData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String = "",
    var importance: Int = 1,
    var note: String = "",
    var created: Long = System.currentTimeMillis(),
    var last_updated: Long = System.currentTimeMillis(),
    var deadline: Long = -1,//-1就是没有
    var complete: Boolean = false,
    var starttime: Long = -1,
    var endtime: Long = -1,
    var isdeleted: Boolean = false
)

@Dao
interface TaskDataDao {

    @Insert
    suspend fun insertTaskData(taskdata: TaskData)

    @Query("SELECT * FROM TaskDataTable")
    suspend fun getAllTaskData(): List<TaskData>

    @Query("SELECT * FROM TaskDataTable WHERE complete = :iscomplete and isdeleted = :isdeleted")
    suspend fun getTaskDataByStatus(iscomplete: Boolean = false, isdeleted: Boolean = false): List<TaskData>?

    @Query("SELECT * FROM TaskDataTable WHERE id = :taskdataId")
    suspend fun getTaskDataById(taskdataId: Int): TaskData?

    @Query("SELECT last_insert_rowid()")
    suspend fun getLastInsertedId(): Long

    @Query("UPDATE TaskDataTable SET " +
            "name = :name, note = :note, last_updated = :lastUpdated, " +
            "starttime = :starttime, endtime = :endtime, complete = :iscomplete, "+
            "isdeleted = :isdeleted WHERE id = :id")
    suspend fun updateTaskDataById(
        id: Int,
        name: String,
        note: String,
        lastUpdated: Long,
        iscomplete: Boolean,
        starttime: Long,
        endtime: Long,
        isdeleted: Boolean
    )

    @Query("UPDATE TaskDataTable SET " +
            "isdeleted = :isdeleted WHERE id = :id")
    suspend fun deleteTaskDataById(
        id: Int,
        isdeleted: Boolean = true
    )
}

@Database(entities = [TaskData::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDataDao

    companion object {
        @Volatile
        private var Instance: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TaskDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

class TaskDataRepository(private val taskdataDao: TaskDataDao) {

    suspend fun addTask(task: TaskData) {
        taskdataDao.insertTaskData(task)
    }

    suspend fun getTaskById(id: Int): TaskData? {
        return taskdataDao.getTaskDataById(id)
    }

    suspend fun getTaskByStatus(completestatus: Boolean, deletestatus: Boolean): List<TaskData>?{
        return taskdataDao.getTaskDataByStatus(completestatus,deletestatus)
    }

    suspend fun getLastId(): Long{
        return taskdataDao.getLastInsertedId()
    }

    suspend fun updateTask(
        id: Int,
        name: String,
        note: String,
        lastUpdated: Long = System.currentTimeMillis(),
        iscomplete: Boolean = false,
        starttime: Long = -1,
        endtime: Long = -1,
        isdeleted: Boolean = false
    ){
        taskdataDao.updateTaskDataById(id, name, note, lastUpdated, iscomplete, starttime, endtime, isdeleted)
    }

    suspend fun deleteTask(id: Int){
        taskdataDao.deleteTaskDataById(id)
    }
}