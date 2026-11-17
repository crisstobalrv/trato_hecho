package com.example.trato_hecho.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trato_hecho.data.model.ClienteEntity
import com.example.trato_hecho.data.model.CotizacionEntity // <- NUEVO

@Database(entities = [ClienteEntity::class, CotizacionEntity::class], version = 2, exportSchema = false) // Incrementamos la versión a 2
abstract class TratoHechoDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao
    abstract fun cotizacionDao(): CotizacionDao // <- NUEVO

    companion object {
        @Volatile
        private var INSTANCE: TratoHechoDatabase? = null

        fun getDatabase(context: Context): TratoHechoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TratoHechoDatabase::class.java,
                    "trato_hecho_database"
                )
                    .fallbackToDestructiveMigration() // Útil en desarrollo si cambia la versión de BD
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}