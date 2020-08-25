package com.example.android.reminder.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface CookDatabaseDao{
    @Insert
    fun insert(cook: Cook)
    @Update
    fun update(cook: Cook)
    @Delete
    fun delete(cook: Cook)
    // returning LiveData gives us a list that will always be up to date without implementing a refresh
    // mechanism
    @Query("Select * From cook ORDER BY last_time_cooked ASC")
    fun getAllCooks(): LiveData<List<Cook>>

    @Query("Select * From cook Where last_time_cooked = :lastTimeCooked")
    fun getCook(lastTimeCooked: Long): Cook

    @Query("Delete From cook")
    fun deleteAllData()
}

@Database(entities = [Cook::class], version = 1, exportSchema = false)
abstract class CookDatabase : RoomDatabase() {
    abstract val cookDatabaseDao: CookDatabaseDao
    companion object{
        // @Volatile means the instance is not cached and it will be always provided form the memory.
        @Volatile
        private var INSTANCE: CookDatabase? = null
        fun getInstance(context: Context): CookDatabase{
            synchronized(this){
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CookDatabase::class.java,
                        "cook").fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
