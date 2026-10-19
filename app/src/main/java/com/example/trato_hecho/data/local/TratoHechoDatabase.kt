package com.example.trato_hecho.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trato_hecho.data.model.ClienteEntity

@Database(entities = [ClienteEntity::class], version = 1, exportSchema = false)
abstract class TratoHechoDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao

    companion object {
        @Volatile
        private var INSTANCE: TratoHechoDatabase? = null

        fun getDatabase(context: Context): TratoHechoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TratoHechoDatabase::class.java,
                    "trato_hecho_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}