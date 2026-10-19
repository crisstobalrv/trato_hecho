package com.example.trato_hecho.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trato_hecho.data.model.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    // SIN la palabra 'suspend' y SIN retornar 'Long'
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarCliente(cliente: ClienteEntity)

    @Query("SELECT * FROM clientes")
    fun obtenerTodosLosClientes(): Flow<List<ClienteEntity>>
}